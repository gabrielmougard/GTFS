package com.algoadvcd.gtfs.core.nio.config;

import java.util.*;

import org.jgrapht.graph.*;

public class GTFSFeedConfigEdge extends DefaultEdge {
	private List<Map<String, String>> dependencies;
	
	public GTFSFeedConfigEdge(List<Map<String, String>> dependencies) {
		this.dependencies = dependencies;
	}
}
