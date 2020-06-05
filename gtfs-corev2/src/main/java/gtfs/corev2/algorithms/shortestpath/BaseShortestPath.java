package gtfs.corev2.algorithms.shortestpath;

import org.jgrapht.Graph;

import java.util.*;

abstract class BaseShortestPath<V, E> implements ShortestPath<V, E> {
	
	protected final Graph<V, E> graph;
	
	public BaseShortestPath(Graph<V, E> graph) {
        this.graph = graph;
    }
	
	@Override
    public Paths<V, E> getPaths(V source) {

        Map<V, GTFSPath<V, E>> paths = new HashMap<>();
        for (V v : graph.vertexSet()) {
            paths.put(v, getPath(source, v));
        }
        return new ListGTFSPaths<>(graph, source, paths);
    }

    @Override
    public double getPathWeight(V source, V target) {
        
    	GTFSPath<V, E> p = getPath(source, target);
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return p.getWeight();
        }
    }

}
