package com.algoadvcd.gtfs.core;

public interface GraphType {

	boolean isDirected();
    
    boolean isUndirected();
    
    boolean isWeighted();
}
