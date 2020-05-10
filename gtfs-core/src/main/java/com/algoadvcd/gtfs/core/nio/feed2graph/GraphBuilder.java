package com.algoadvcd.gtfs.core.nio.feed2graph;

import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

import tech.tablesaw.api.*;

import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigEdge;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.GTFSFeed;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.GTFSReader;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.GTFSReaderBuilder;
import com.algoadvcd.gtfs.core.util.Tuple;


public class GraphBuilder {
	
	private static final Logger logger = LogManager.getLogger();
	private String pathtodataset;
	private String mode; // either "local" or "remote"
	
	private double connectionThreshold;
	private double walkSpeedKmph;
	private int fallbackStopCost;
	private boolean interpolateTimes;
	private boolean imputeWalkTransfers;
	
	public GraphBuilder(String pathtodataset, String mode) {
		this.pathtodataset = pathtodataset;
		this.mode = mode;
		
		//default param used for loadfeedasgraph
		this.connectionThreshold = 50.0;
		this.walkSpeedKmph = 4.5;
		this.fallbackStopCost = 30*60;
		this.imputeWalkTransfers = true;
		this.interpolateTimes = true;
		
	}
	
	public Graph<> loadFeedAsGraph(
			GTFSFeed feed,
			Integer startTime,
			Integer endTime,
			String name) {
		
		if (name == null) {
			name = Util.generateRamdomName();
		}
		
		if (startTime < 0 || endTime < 0) {
			throw GTFSExceptions.InvalidTimeBracket("Invalid start or end target times provided.");
		}
		
		if (endTime <= startTime) {
			throw GTFSExceptions.InvalidTimeBracket("Invalid ordering: Start time is greater than end time.");
		}
		
		Class[] stopCostMethodParameterTypes = new Class[1];
		stopCostMethodParameterTypes[0] = Double.class;
		stopCostMethodParameterTypes[1] = Double.class;
		stopCostMethodParameterTypes[2] = new ArrayList<Double>().getClass(); // https://stackoverflow.com/questions/4685563/how-to-pass-a-function-as-a-parameter-in-java
		
		Tuple<Table, Table> summaryEdgeCostsAndWaitTimesByStop = 
				new GraphSummarizer()
				.generateSummaryGraphElements(
						feed,
						startTime,
						endTime,
						this.fallbackStopCost,
						this.interpolateTimes,
						GraphBuilder.class.getMethod("calculateMeansDefault", stopCostMethodParameterTypes)
				);
		
		Graph<GTFSVertex, GTFSEdge> g = generateEmptyMdGraph();
		
		return populateGraph(
				g,
				name,
				feed,
				summaryEdgeCostsAndWaitTimesByStop.second,
				summaryEdgeCostsAndWaitTimesByStop.first,
				this.connectionThreshold,
				this.walkSpeedKmph,
				this.imputeWalkTransfers
		);
				
		
		
	}
	
	public GTFSFeed getRepresentativeFeed() {
		
		GTFSFeed res;
		switch(this.mode) {
		case "local":
			logger.info("loading mode : 'local' detected.");
			GTFSReader reader = 
					 new GTFSReaderBuilder(this.pathtodataset)
					 .localReader()
					 .build();
			logger.info("load serviceIdsByDate");
			Map<Date, Set<String>> serviceIdsByDate = reader.readServiceIdsByDate();
			logger.info("load tripCountsByDate");
			Map<Date, Integer> tripCountsByDate = reader.readTripCountsByDate();
			
			//choose the service_id that has the most trips associated with it
			Entry<Date, Integer> maxTripCount = 
					Collections
					.max(
							tripCountsByDate.entrySet(), 
							(
									Entry<Date, Integer> e1, 
									Entry<Date, Integer> e2) -> e1.getValue().compareTo(e2.getValue()
							)
					);
			logger.info("Selected date : "+maxTripCount.getKey().toString());
			logger.info("Number of trips on that date: "+maxTripCount.getValue());
			logger.info("All related service IDs: : "+String.join("\n\t", serviceIdsByDate.get(maxTripCount.getKey())));
	
			Set<String> sub = serviceIdsByDate.get(maxTripCount.getKey());
			Map<String, Map<String, Set<String>>> feedQuery = new HashMap<String, Map<String, Set<String>>>();
			Map<String, Set<String>> arg = new HashMap<String, Set<String>>();
			arg.put("service_id", sub);
			feedQuery.put("trips.txt", arg);
			
			return reader.loadFeed(this.pathtodataset, feedQuery);
			
		case "remote":
			logger.info("the loading mode : 'remote' is still in development.");
			res = null;
			break;
		default:
			logger.info("the loading mode : '"+this.mode+"' is not supported yet or recognized.");
			res = null;
			break;
		}
		return res;
	}
	
	private Graph<GTFSVertex, GTFSEdge> generateEmptyMdGraph() {
		Graph<GTFSVertex, GTFSEdge> g = new DirectedMultigraph(GTFSEdge.class);
		return g;
	}
	
	private Double calculateMeansDefault(
			Double targetTimeStart,
			Double targetTimeEnd,
			List<Double> arrivalTimes) {
		
		if (arrivalTimes.size() < 2) {
			logger.info("not enough arrival times");
			return 0.00;
		}
		
		//TODO : continue implementation
		Collections.sort(arrivalTimes); //sort the arrivalTimes list
		List<Double> first = arrivalTimes.subList(1, arrivalTimes.size());
		List<Double> second = arrivalTimes.subList(0, arrivalTimes.size()-1);
		List<Double> waitSeconds = new ArrayList<Double>();
		Iterator<Double> itFirst = first.iterator();
		Iterator<Double> itSecond = second.iterator();
		while(itFirst.hasNext() && itSecond.hasNext()) {
			waitSeconds.add(itFirst.next() - itSecond.next());
		}
		
		Double fromStartTimeToFirstArrival = arrivalTimes.get(0) - targetTimeStart;
		waitSeconds.add(fromStartTimeToFirstArrival);
		Double fromLastArrivalToEndTime = targetTimeEnd - arrivalTimes.get(arrivalTimes.size()-1);
		waitSeconds.add(fromLastArrivalToEndTime);
		
		//prune waitSeconds for negative values
		waitSeconds.removeIf(s -> s < 0);
		// Naive implementation: halve the headway to get average wait time
		OptionalDouble avgWait = waitSeconds.stream()
	            .mapToDouble(a -> a)
	            .average();
		return avgWait.isPresent() ? avgWait.getAsDouble()/2 : 0.0;
  			
	}
}
