package com.algoadvcd.gtfs.core.algorithms.shortestpath;

import com.algoadvcd.gtfs.core.Graph;

import java.util.*;

public class BFSShortestPath<GTFSVertex> {
	
	private Map<GTFSVertex, Set<GTFSVertex>> verticesMap;
	private GTFSVertex src;
	private GTFSVertex dest;
	
	public BFSShortestPath(Graph<GTFSVertex> graph, GTFSVertex src, GTFSVertex dest) {
        this.verticesMap = graph.getVerticesMap();
        this.src = src;
        this.dest = dest;
    }
	
	public List<GTFSVertex> getPath() {
		//Initialization
		Map<GTFSVertex, GTFSVertex> nextVertexMap = new HashMap<GTFSVertex, GTFSVertex>();
		GTFSVertex currentVertex = this.src;
		GTFSVertex previousVertex = this.src;
		
		//Queue
		Queue<GTFSVertex> queue = new LinkedList<GTFSVertex>();
		queue.add(currentVertex);
		
		Set<GTFSVertex> visitedVertices = new HashSet<GTFSVertex>();
		visitedVertices.add(currentVertex);
		
		while(!queue.isEmpty()) {
			currentVertex = queue.remove();
			if (currentVertex.equals(this.dest)) {
				if (!previousVertex.equals(currentVertex)) {
					nextVertexMap.put(previousVertex, currentVertex);
			    }
			    break;
			} else {
				for (GTFSVertex nextVertex : this.verticesMap.get(currentVertex)) {
					if (!visitedVertices.contains(nextVertex)) {
						queue.add(nextVertex);
						visitedVertices.add(nextVertex);
						
						//Look up of next vertex instead of previous
						nextVertexMap.put(currentVertex,  nextVertex);
						previousVertex = currentVertex;
					}
				}
			}
		}
		
		if (!currentVertex.equals(this.dest)) {
	        throw new RuntimeException("No feasible path.");
	    }
		
		//Reconstruct the path
		List<GTFSVertex> path = new LinkedList<GTFSVertex>();
		for (GTFSVertex v = this.src; v != null; v = nextVertexMap.get(v)) {
			path.add(v);
		}
		
		return path;
	}
}
