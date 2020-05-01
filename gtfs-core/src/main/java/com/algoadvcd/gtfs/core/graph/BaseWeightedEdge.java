package com.algoadvcd.gtfs.core.graph;

import com.algoadvcd.gtfs.core.Graph;

class BaseWeightedEdge extends BaseEdge {
    private static final long serialVersionUID = 2L;
    
    double weight = Graph.DEFAULT_EDGE_WEIGHT;
}
