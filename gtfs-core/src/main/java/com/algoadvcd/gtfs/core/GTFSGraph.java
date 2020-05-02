package com.algoadvcd.gtfs.core;

import java.util.*;

public class GTFSGraph<GTFSVertex> implements Graph<GTFSVertex> {
	private Map<GTFSVertex, Set<GTFSVertex>> verticesMap;
    private int edgesCount;
    
    public GTFSGraph() {
        verticesMap = new HashMap<>();
    }
    
    public int getNumVertices() {
        return verticesMap.size();
    }

    public int getNumEdges() {
        return edgesCount;
    }


    private void validateVertex(GTFSVertex v) {
        if (!hasVertex(v)) throw new IllegalArgumentException(v.toString() + " is not a vertex");
    }
    public int degree(GTFSVertex v) {
        validateVertex(v);
        return verticesMap.get(v).size();
    }

    public void addEdge(GTFSVertex v, GTFSVertex w) {
        if (!hasVertex(v)) addVertex(v);
        if (!hasVertex(w)) addVertex(w);
        if (!hasEdge(v, w)) edgesCount++;
        verticesMap.get(v).add(w);
        verticesMap.get(w).add(v);
    }

    public void addVertex(GTFSVertex v) {
        if (!hasVertex(v)) verticesMap.put(v, new HashSet<GTFSVertex>());
    }

    public boolean hasEdge(GTFSVertex v, GTFSVertex w) {
        validateVertex(v);
        validateVertex(w);
        return verticesMap.get(v).contains(w);
    }

    public boolean hasVertex(GTFSVertex v) {
        return verticesMap.containsKey(v);
    }

    @Override
    public Iterator<GTFSVertex> iterator() {
        return verticesMap.keySet().iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (GTFSVertex v: verticesMap.keySet()) {
            builder.append(v.toString() + ": ");
            for (GTFSVertex w: verticesMap.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}
