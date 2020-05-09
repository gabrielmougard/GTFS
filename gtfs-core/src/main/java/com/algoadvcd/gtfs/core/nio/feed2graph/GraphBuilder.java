package com.algoadvcd.gtfs.core.nio.feed2graph;

import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algoadvcd.gtfs.core.nio.gtfs2feed.GTFSFeed;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.GTFSReader;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.GTFSReaderBuilder;


public class GraphBuilder {
	
	private static final Logger logger = LogManager.getLogger();
	private String pathtodataset;
	private String mode; // either "local" or "remote"
	
	public GraphBuilder(String pathtodataset, String mode) {
		this.pathtodataset = pathtodataset;
		this.mode = mode;
	}
	
	public loadFeedAsGraph() {
		
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
	
	private generateEmptyMdGraph() {
		
	}
	
	private double calculateMeansDefault(
			double targetTimeStart,
			double targetTimeEnd,
			List<Double> arrivalTimes) {
		
		if (arrivalTimes.size() < 2) {
			logger.info("not enough arrival times");
			return 0.00;
		}
		
		//TODO : continue implementation
		
		
	}
}
