package gtfs.corev2.algorithms.shortestpath;

import java.io.Serializable;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.GraphWalk;

import gtfs.corev2.algorithms.shortestpath.ShortestPath.Paths;
import gtfs.corev2.nio.util.Tuple;

public class TreeGTFSPaths<V, E> implements Paths<V, E>, Serializable {

	private static final long serialVersionUID = 9869696595L;
    protected Graph<V, E> g;
    protected V source;

    /**
     * map which keeps for each target vertex the parent edge and the length of the shortest path.
     */
    protected Map<V, Tuple<Double, E>> map;
    
    public TreeGTFSPaths(Graph<V, E> g, V source, Map<V, Tuple<Double, E>> distParentMap) {
            
    		this.g = g;
            this.source = source;
            this.map = distParentMap;
    }
    

    @Override
    public V getSourceVertex() {
        return source;
    }

    @Override
    public double getWeight(V targetVertex) {
        Tuple<Double, E> p = map.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return p.first;
        }
    }
    
    @Override
    public GTFSPath<V, E> getPath(V targetVertex) {
   
    	if (source.equals(targetVertex)) {
            return GTFSPathImpl.singletonWalk(g, source, 0d);
        }
    	
        LinkedList<E> edgeList = new LinkedList<>();

        V cur = targetVertex;
        Tuple<Double, E> p = map.get(cur);
        if (p == null || p.first.equals(Double.POSITIVE_INFINITY)) {
            return null;
        }

        double weight = 0d;
        while (p != null && !cur.equals(source)) {
            E e = p.second;
            if (e == null) {
                break;
            }
            edgeList.addFirst(e);
            weight += g.getEdgeWeight(e);
            
            //get opposite vertex
            cur = Graphs.getOppositeVertex(g, e, cur);      
            //

            p = map.get(cur);
        }

        return new GTFSPathImpl<>(g, source, targetVertex, null, edgeList, weight);
    }
    
}
