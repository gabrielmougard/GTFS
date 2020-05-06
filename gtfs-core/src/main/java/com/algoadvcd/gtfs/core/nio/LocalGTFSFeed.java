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
							gtfsTables.get(key), 
							this.gtfsTablesLock, 
							this.config
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
		
	}
	
	public Table get(String tablename) {
		return gtfsTables.get(tablename);
	}
	
	class ComputeFeedTask implements Runnable {
		private String tablename;
		private Table gtfsTable;
		private Lock gtfsTablesLock;
		private Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> config;
		
		public ComputeFeedTask(String tablename, Table gtfsTable, Lock gtfsTablesLock, Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> config) {
			this.tablename = tablename;
			this.gtfsTable = gtfsTable;
			this.gtfsTablesLock = gtfsTablesLock;
			this.config = config;
		}
		
		public void run() {
			InputStream fileStream = 
					getClass()
					.getClassLoader()
					.getResourceAsStream(this.tablename+".txt");
			
			try {
				Table t = Table.read().csv(fileStream, this.tablename);
				
				//1) prune table to keep only the data that we want to use
				t = prune(t);
				//2) lock gtfsTable, update gtfsTable, unlock gtfsTable
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private Table prune(Table t) {
			/*
			 * Depth-first search through the dependency graph
			 * and prune dependent Table along the way
			 */
		
			//1) find the vertex associated with the table t in the dependency graph
			
			//2) get the neighbors edges of this vertex
			
			//3) iterate through the edges found 
				
				//3.1) get the "dependencies" of the edge.
			
				//3.2
			
		}
	}
	
}
