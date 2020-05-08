package com.algoadvcd.gtfs.core.nio.gtfs2feed;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;

import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigEdge;
import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigVertex;

import tech.tablesaw.api.Table;

public class RemoteGTFSFeed implements GTFSFeed {
	private static Logger logger = LogManager.getLogger();
	private String source;
	private Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> config;
	private Map<String, Table> cache;
	private Map<String, String> pathmap;
	private Map<String, Map<String, Object>> view;
	private boolean delete_after_reading;
	private Lock gtfsTablesLock = new ReentrantLock(); //because of concurrent write to gtfsTable
	private Map<String, Table> gtfsTables;
	
	public RemoteGTFSFeed(String source) {
		
	}
	
	public Table get(String tablename) {
		return gtfsTables.get(tablename);
	}

	public void fetch() {
		// TODO 
		
	}
}
