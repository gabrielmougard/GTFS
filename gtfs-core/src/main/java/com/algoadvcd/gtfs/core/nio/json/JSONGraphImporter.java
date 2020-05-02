package com.algoadvcd.gtfs.core.nio.json;

import java.io.*;
import java.util.*;

import com.algoadvcd.gtfs.core.Graph;
import com.algoadvcd.gtfs.core.nio.GraphImporter;


public class JSONGraphImporter<GTFSVertex> implements GraphImporter<GTFSVertex> {
		
	/*
	 * the target is meant to be on the resources
	 */
	public void importGraphLocal(Graph<GTFSVertex> g, String target) {
		InputStream inputStream = 
				getClass()
				.getClassLoader()
				.getResourceAsStream(target);
		
		JsonObjectIterator iterator = new JsonObjectIterator(inputStream);
		while(iterator.hasNext()) {
			// TODO
			//add to the graph
		}
		
	}

	/*
	 * the remote access is google cloud bucket
	 */
	public void importGraphRemote(Graph<GTFSVertex> g, String target) {
		//bucket connection
		
		/*
		 * Blob blob = bucket.get("some-file");
		 * ReadChannel reader = blob.reader();
		 * InputStream inputStream = Channels.newInputStream(reader);
		 */
		
		return;
	}
	
}
