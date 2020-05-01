package com.algoadvcd.gtfs.core;

import com.algoadvcd.gtfs.core.graph.*;
import com.algoadvcd.gtfs.core.util.*;

import java.util.*;
import java.util.function.*;

public abstract class Graphs {

	public static <V, E> E addEdge(Graph<V, E> g, V sourceVertex, V targetVertex, double weight) {
        Supplier<E> edgeSupplier = g.getEdgeSupplier();
        if (edgeSupplier == null) {
            throw new UnsupportedOperationException("Graph contains no edge supplier");
        }
        E e = edgeSupplier.get();

        if (g.addEdge(sourceVertex, targetVertex, e)) {
            g.setEdgeWeight(e, weight);
            return e;
        } else {
            return null;
        }
    }
	
	public static <V, E> E addEdgeWithVertices(Graph<V, E> g, V sourceVertex, V targetVertex) {
        g.addVertex(sourceVertex);
        g.addVertex(targetVertex);

        return g.addEdge(sourceVertex, targetVertex);
    }
	
	public static <V, E> boolean addEdgeWithVertices(Graph<V, E> targetGraph, 
													 Graph<V, E> sourceGraph, 
													 E edge) {
		V sourceVertex = sourceGraph.getEdgeSource(edge);
		V targetVertex = sourceGraph.getEdgeTarget(edge);

		targetGraph.addVertex(sourceVertex);
		targetGraph.addVertex(targetVertex);

		return targetGraph.addEdge(sourceVertex, targetVertex, edge);
	}
	
	public static <V, E> E addEdgeWithVertices(Graph<V, E> g, 
											   V sourceVertex, 
											   V targetVertex, 
											   double weight) {
		g.addVertex(sourceVertex);
		g.addVertex(targetVertex);

		return addEdge(g, sourceVertex, targetVertex, weight);
	}
	
	public static <V, E> boolean addGraph(Graph<? super V, ? super E> destination, 
										  Graph<V, E> source) {
		boolean modified = addAllVertices(destination, source.vertexSet());
		modified |= addAllEdges(destination, source, source.edgeSet());

		return modified;
	}
	
	public static <V, E> void addGraphReversed(Graph<? super V, ? super E> destination, 
											   Graph<V, E> source) {
		if (!source.getType().isDirected() || !destination.getType().isDirected()) {
			throw new IllegalArgumentException("graph must be directed");
		}

		addAllVertices(destination, source.vertexSet());
    
		for (E edge : source.edgeSet()) {
			destination.addEdge(source.getEdgeTarget(edge), source.getEdgeSource(edge));
		}
	}
	
	public static <V, E> boolean addAllEdges(
	        Graph<? super V, ? super E> destination, 
	        Graph<V, E> source, 
	        Collection<? extends E> edges) {
	        
				boolean modified = false;

				for (E e : edges) {
		            V s = source.getEdgeSource(e);
		            V t = source.getEdgeTarget(e);
		            destination.addVertex(s);
		            destination.addVertex(t);
		            modified |= destination.addEdge(s, t, e);
				}

				return modified;
	}
	
	public static <V, E> boolean addAllVertices(
	        Graph<? super V, ? super E> destination, 
	        Collection<? extends V> vertices) {
	        
				boolean modified = false;

		        for (V v : vertices) {
		            modified |= destination.addVertex(v);
		        }

		        return modified;
	}
	
	public static <V, E> List<V> neighborListOf(
			Graph<V, E> g, V vertex) {
        
				List<V> neighbors = new ArrayList<>();
		
		        for (E e : g.edgesOf(vertex)) {
		            neighbors.add(getOppositeVertex(g, e, vertex));
		        }
		
		        return neighbors;
    }
	
	public static <V, E> Set<V> neighborSetOf(Graph<V, E> g, V vertex) {
        Set<V> neighbors = new LinkedHashSet<>();

        for (E e : g.edgesOf(vertex)) {
            neighbors.add(Graphs.getOppositeVertex(g, e, vertex));
        }

        return neighbors;
    }
	
	public static <V, E> List<V> predecessorListOf(Graph<V, E> g, V vertex) {
        List<V> predecessors = new ArrayList<>();
        Set<? extends E> edges = g.incomingEdgesOf(vertex);

        for (E e : edges) {
            predecessors.add(getOppositeVertex(g, e, vertex));
        }

        return predecessors;
    }
	
	public static <V, E> List<V> successorListOf(Graph<V, E> g, V vertex) {
        List<V> successors = new ArrayList<>();
        Set<? extends E> edges = g.outgoingEdgesOf(vertex);

        for (E e : edges) {
            successors.add(getOppositeVertex(g, e, vertex));
        }

        return successors;
    }
	
	public static <V, E> boolean testIncidence(Graph<V, E> g, E e, V v) {
        return (g.getEdgeSource(e).equals(v)) || (g.getEdgeTarget(e).equals(v));
    }
	
	public static <V, E> V getOppositeVertex(Graph<V, E> g, E e, V v) {
        
		V source = g.getEdgeSource(e);
        V target = g.getEdgeTarget(e);
        if (v.equals(source)) {
            return target;
        } else if (v.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException("no such vertex: " + v.toString());
        }
    }
	
	public static <V, E> boolean removeVertexAndPreserveConnectivity(Graph<V, E> graph, V vertex) {
        if (!graph.containsVertex(vertex)) {
            return false;
        }

        if (vertexHasPredecessors(graph, vertex)) {
            List<V> predecessors = Graphs.predecessorListOf(graph, vertex);
            List<V> successors = Graphs.successorListOf(graph, vertex);

            for (V predecessor : predecessors) {
                addOutgoingEdges(graph, predecessor, successors);
            }
        }

        graph.removeVertex(vertex);
        return true;
    }
	
	public static <V, E> boolean removeVerticesAndPreserveConnectivity(Graph<V, E> graph, Predicate<V> predicate) {
    
		List<V> verticesToRemove = new ArrayList<>();

	    for (V node : graph.vertexSet()) {
	        if (predicate.test(node)) {
	            verticesToRemove.add(node);
	        }
	    }
	
	    return removeVertexAndPreserveConnectivity(graph, verticesToRemove);
	}
	
	public static <V, E> boolean removeVertexAndPreserveConnectivity(Graph<V, E> graph, Iterable<V> vertices) {
    
		boolean atLeastOneVertexHasBeenRemoved = false;

	    for (V vertex : vertices) {
	        if (removeVertexAndPreserveConnectivity(graph, vertex)) {
	            atLeastOneVertexHasBeenRemoved = true;
	        }
	    }
	
	    return atLeastOneVertexHasBeenRemoved;
	}
	
	public static <V, E> void addOutgoingEdges(Graph<V, E> graph, V source, Iterable<V> targets) {
        
		if (!graph.containsVertex(source)) {
            graph.addVertex(source);
        }
        for (V target : targets) {
            if (!graph.containsVertex(target)) {
                graph.addVertex(target);
            }
            graph.addEdge(source, target);
        }
    }
	
	public static <V, E> void addIncomingEdges(Graph<V, E> graph, V target, Iterable<V> sources) {
        if (!graph.containsVertex(target)) {
            graph.addVertex(target);
        }
        for (V source : sources) {
            if (!graph.containsVertex(source)) {
                graph.addVertex(source);
            }
            graph.addEdge(source, target);
        }
    }
	
	public static <V, E> boolean vertexHasSuccessors(Graph<V, E> graph, V vertex) {
        return !graph.outgoingEdgesOf(vertex).isEmpty();
    }
	
	public static <V, E> boolean vertexHasPredecessors(Graph<V, E> graph, V vertex) {
        return !graph.incomingEdgesOf(vertex).isEmpty();
    }
	
	

}
