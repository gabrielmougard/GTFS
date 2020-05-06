package com.algoadvcd.gtfs.core.nio;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jgrapht.Graph;

import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigEdge;
import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigVertex;
import com.algoadvcd.gtfs.core.nio.config.GTFSFeedGraphConfig;
import com.algoadvcd.gtfs.core.util.Tuple;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalGTFSFeed implements GTFSFeed {
	
	private static Logger logger = LogManager.getLogger();
	private String source;
	private Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> config;
	private Map<String, Table> cache;
	private Map<String, String> pathmap;
	private Map<String, Map<String, Object>> view;
	private boolean delete_after_reading;
	/* mutex ? (since we plan to read concurrently the input stream)
	 * reentrant lock : https://www.geeksforgeeks.org/reentrant-lock-java/
	 * http://lifeinide.com/post/2011-05-25-threaded-iostreams-in-java/
	 */
	private Lock gtfsTablesLock = new ReentrantLock(); //because of concurrent write to gtfsTable
	private Map<String, Table> gtfsTables;
	
	
	public LocalGTFSFeed(String source) {
		this.source = source;
		this.config = GTFSFeedGraphConfig.defaultConfig();
		
		//initialize gtfsTables
		this.gtfsTables = new HashMap<String, Table>();
		this.gtfsTables.put("agency", Table.create("agency"));
		this.gtfsTables.put("calendar", Table.create("calendar"));
		this.gtfsTables.put("calendar_dates", Table.create("calendar_dates"));
		this.gtfsTables.put("fare_attributes", Table.create("fare_attributes"));
		this.gtfsTables.put("fare_rules", Table.create("fare_rules"));
		this.gtfsTables.put("feed_info", Table.create("feed_info"));
		this.gtfsTables.put("frequencies", Table.create("frequencies"));
		this.gtfsTables.put("routes", Table.create("routes"));
		this.gtfsTables.put("shapes", Table.create("shapes"));
		this.gtfsTables.put("stops", Table.create("stops"));
		this.gtfsTables.put("stop_times", Table.create("stop_times"));
		this.gtfsTables.put("transfers", Table.create("transfers"));
		this.gtfsTables.put("trips", Table.create("trips"));
		
	}
	
	public LocalGTFSFeed(LocalGTFSFeed feed) {
		//If needed but not sure of that...
	}
	
	public void fetch() {
		// call concurrent code for updating gtfsTables
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.gtfsTables.size());
		for (String key : gtfsTables.keySet()) {
			ComputeFeedTask task = 
					new ComputeFeedTask
					(
							key, 
							this.gtfsTablesLock, 
							this.gtfsTables
					);
			logger.info("ComputeFeedTask created for : "+key);
			executor.execute(task);
		}
		executor.shutdown();
		try {
	         if (!executor.awaitTermination(6000, TimeUnit.MILLISECONDS)) {
	        	 logger.info("ThreadPoolExecutor shutdown now. ");
	        	 executor.shutdownNow();
	         }                  
	    } catch (InterruptedException e) {  
	    	logger.error("ThreadPoolExecutor error : "+e);
	        executor.shutdownNow();
	    }
		
		//pruning tables in order to keep only the data that we really need
		for (String k: this.gtfsTables.keySet()) {
			prune(this.gtfsTables.get(k));
		}
		
	}
	
	private void prune(Table originTable) {
		/*
		 * Depth-first search through the dependency graph
		 * and prune dependent Table along the way
		 */
		
		//1) find the vertex associated with the table t in the dependency graph
		Set<GTFSFeedConfigVertex> vertices = this.config.vertexSet();
		GTFSFeedConfigVertex tableVertex = null;
		for (GTFSFeedConfigVertex v : vertices) {
			if (v.getFilename() == originTable.name()) {
				tableVertex = v;
			}
		}
		
		//2) get the neighbors edges of this vertex
		List<Tuple<GTFSFeedConfigVertex,List<Map<String, String>>>> dependenciesList = new ArrayList<Tuple<GTFSFeedConfigVertex,List<Map<String, String>>>>();
		Set<GTFSFeedConfigEdge> adjacentEdges = this.config.edgesOf(tableVertex);
		
		for (GTFSFeedConfigEdge e : adjacentEdges) {
			List<Map<String, String>> dep = e.getDependencies();
			Tuple<GTFSFeedConfigVertex,List<Map<String, String>>> tuple = new Tuple<GTFSFeedConfigVertex,List<Map<String, String>>>(e.getTarget(), dep);
			dependenciesList.add(tuple);
		}
		
		for (Tuple<GTFSFeedConfigVertex,List<Map<String, String>>> tup : dependenciesList) {
			GTFSFeedConfigVertex target = tup.first;
			String targetName = target.getFilename().split(".")[0];
			Table depdf = this.gtfsTables.get(targetName);
			for (Map<String, String> deps : tup.second) {
				//TODO : finish this 
			}
		}
		
	}
	
	public Table get(String tablename) {
		return gtfsTables.get(tablename);
	}
	
	class ComputeFeedTask implements Runnable {
		private String tablename;
		private Lock gtfsTablesLock;
		private Map<String, Table> gtfsTables;
		
		public ComputeFeedTask(String tablename, Lock gtfsTablesLock, Map<String, Table> gtfsTables) {
			this.tablename = tablename;
			this.gtfsTablesLock = gtfsTablesLock;
			this.gtfsTables = gtfsTables;
		}
		
		public void run() {
			InputStream fileStream = 
					getClass()
					.getClassLoader()
					.getResourceAsStream(this.tablename+".txt");
			
			try {
				Table t = Table.read().csv(fileStream, this.tablename);
				
				//1) prune table to keep only the data that we want to use
				//t = prune(t);
				//2) lock gtfsTable, update gtfsTable, unlock gtfsTable
				gtfsTablesLock.lock(); 
		        
				try { 
		            //update the list of table (which was an empty table before)
		            this.gtfsTables.put(this.tablename, t);
		        } catch(Exception e) { 
		            e.printStackTrace(); 
		        } finally { 
		        	gtfsTablesLock.unlock(); 
		        } 
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
}
