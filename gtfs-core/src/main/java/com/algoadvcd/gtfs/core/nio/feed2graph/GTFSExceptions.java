package com.algoadvcd.gtfs.core.nio.feed2graph;

public class GTFSExceptions {
	//write our custom exceptions here
	static class InvalidTimeBracket  extends Exception {
		InvalidTimeBracket(String s) {
			super(s);
		}
	}
	
	static class InvalidGTFS  extends Exception {
		InvalidGTFS(String s) {
			super(s);
		}
	}
}
