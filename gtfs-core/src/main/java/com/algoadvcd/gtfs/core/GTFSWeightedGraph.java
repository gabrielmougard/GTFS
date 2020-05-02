package com.algoadvcd.gtfs.core;

import java.util.*;

public class GTFSWeightedGraph<GTFSVertex extends Comparable<GTFSVertex>> implements Iterable<GTFSVertex> {
	
	private ArrayList<GTFSVertex> vertices;
	private ArrayList<Edge> edges;
	
	public GTFSWeightedGraph() {
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
	}
	
	public int getNumVertices() {
        return vertices.size();
    }

    public int getNumEdges() {
        return edges.size();
    }  
    
    public void addEdge(GTFSVertex v, GTFSVertex w, double weight) {
		
		Edge e = new Edge(v, w, weight);
		edges.add(e);
		
    }
    
    private GTFSVertex findVertex(GTFSVertex v) {
		for (GTFSVertex each : vertices) {
			if (each.compareTo(v) == 0)
				return each;
		}
		return null;
	}
    
    private Edge findEdge(GTFSVertex v1, GTFSVertex v2) {
		for (Edge each : edges) {
			if (each.from.equals(v1) && each.to.equals(v2)) {
				return each;
			}
		}
		return null;
	}
    
    @Override
    public Iterator<GTFSVertex> iterator() {
        return vertices.iterator();
    }

    
    @Override
	public String toString()
	{
		String retval = "";
		for (GTFSVertex each : vertices)
		{
			retval += each.toString() + "\n";
		}
		return retval;
	}
	
	
	class Edge {
		
		GTFSVertex from;
		GTFSVertex to;
		double weight;
		
		public Edge(GTFSVertex v1, GTFSVertex v2, double weight) {
			from = findVertex(v1);
			if (from == null)
			{
				from = v1;
				vertices.add(from);
			}
			to = findVertex(v2);
			if (to == null)
			{
				to = v2;
				vertices.add(to);
			}
			this.weight = weight;

		}
		
		@Override
		public String toString() {
			return "Edge From: " + from.toString() + " to: " + to.toString() + " weight: " + weight;
		}
	}
}
