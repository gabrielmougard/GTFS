package gtfs.corev2.algorithms.shortestpath;

import java.io.Serializable;
import java.util.*;

import org.jgrapht.Graph;

import gtfs.corev2.algorithms.shortestpath.ShortestPath.Paths;

public class ListGTFSPaths<V, E> implements Paths<V, E>, Serializable{
	
	private static final long serialVersionUID = 987797999L;

    /**
     * The graph
     */
    protected Graph<V, E> graph;

    /**
     * The source vertex of all paths
     */
    protected V source;

    /**
     * One path per vertex
     */
    protected Map<V, GTFSPath<V, E>> paths;

    /**
     * Construct a new instance.
     */
    public ListGTFSPaths(Graph<V, E> graph, V source, Map<V, GTFSPath<V, E>> paths) {
        this.graph = graph;
        this.source = source;
        this.paths = paths;
    }
    

    @Override
    public V getSourceVertex()
    {
        return source;
    }

    @Override
    public double getWeight(V targetVertex) {
        GTFSPath<V, E> p = paths.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return p.getWeight();
        }
    }
    
    @Override
    public GTFSPath<V, E> getPath(V targetVertex) {
        GTFSPath<V, E> p = paths.get(targetVertex);
        if (p == null) {
            return null;
        } else {
            return p;
        }
    }
}
