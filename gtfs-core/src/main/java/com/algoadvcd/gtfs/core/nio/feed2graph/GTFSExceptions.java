package com.algoadvcd.gtfs.core.nio.feed2graph;

public class GTFSExceptions {
	//write our custom exceptions here
	public static InvalidTimeBracket InvalidTimeBracket(String s) {
		return InvalidTimeBracket(s);
	}
	
	
	
	
	
	//excetions inner classes
	public static class InvalidTimeBracket  extends Exception {
		InvalidTimeBracket(String s) {
			super(s);
		}
	}
	
	public static class InvalidGTFS  extends Exception {
		InvalidGTFS(String s) {
			super(s);
		}
	}
}
