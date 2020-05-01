package com.algoadvcd.gtfs.core.graph;

import com.algoadvcd.gtfs.core.*;
import com.algoadvcd.gtfs.core.util.*;
import java.util.*;

/*
* @param <V> the graph vertex type
* @param <E> the graph edge type
*/

public abstract class AbstractGraph<V, E>
	implements Graph<V, E> {
	
	protected AbstractGraph() {}
	
	@Override
	public boolean containsEdge(V sourceVertex, V targetVertex) {
        return getEdge(sourceVertex, targetVertex) != null;
    }
	
	@Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        boolean modified = false;

        for (E e : edges) {
            modified |= removeEdge(e);
        }

        return modified;
    }
	
	@Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        Set<E> removed = getAllEdges(sourceVertex, targetVertex);
        if (removed == null) {
            return null;
        }
        removeAllEdges(removed);

        return removed;
    }
	
	@Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean modified = false;

        for (V v : vertices) {
            modified |= removeVertex(v);
        }

        return modified;
    }
	
	@Override
    public String toString()
    {
        return toStringFromSets(vertexSet(), edgeSet(), this.getType().isDirected());
    }
	
	protected boolean assertVertexExist(V v)
    {
        if (containsVertex(v)) {
            return true;
        } else if (v == null) {
            throw new NullPointerException();
        } else {
            throw new IllegalArgumentException("no such vertex in graph: " + v.toString());
        }
    }
	
	protected boolean removeAllEdges(E[] edges)
    {
        boolean modified = false;

        for (E edge : edges) {
            modified |= removeEdge(edge);
        }

        return modified;
    }
	
	protected String toStringFromSets(
	        Collection<? extends V> vertexSet, Collection<? extends E> edgeSet, boolean directed)
	    {
	        List<String> renderedEdges = new ArrayList<>();

	        StringBuilder sb = new StringBuilder();
	        for (E e : edgeSet) {
	            if ((e.getClass() != Edge.class)
	                && (e.getClass() != WeightedEdge.class))
	            {
	                sb.append(e.toString());
	                sb.append("=");
	            }
	            if (directed) {
	                sb.append("(");
	            } else {
	                sb.append("{");
	            }
	            sb.append(getEdgeSource(e));
	            sb.append(",");
	            sb.append(getEdgeTarget(e));
	            if (directed) {
	                sb.append(")");
	            } else {
	                sb.append("}");
	            }

	            // REVIEW jvs 29-May-2006: dump weight somewhere?
	            renderedEdges.add(sb.toString());
	            sb.setLength(0);
	        }

	        return "(" + vertexSet + ", " + renderedEdges + ")";
	    }
	
	@Override
    public int hashCode()
    {
        int hash = vertexSet().hashCode();

        for (E e : edgeSet()) {
            int part = e.hashCode();

            int source = getEdgeSource(e).hashCode();
            int target = getEdgeTarget(e).hashCode();

            // see http://en.wikipedia.org/wiki/Pairing_function (VK);
            int pairing = ((source + target) * (source + target + 1) / 2) + target;
            part = (27 * part) + pairing;

            long weight = (long) getEdgeWeight(e);
            part = (27 * part) + (int) (weight ^ (weight >>> 32));

            hash += part;
        }

        return hash;
    }
	
	@Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Graph<V, E> g = TypeUtil.uncheckedCast(obj);

        if (!vertexSet().equals(g.vertexSet())) {
            return false;
        }
        if (edgeSet().size() != g.edgeSet().size()) {
            return false;
        }

        for (E e : edgeSet()) {
            V source = getEdgeSource(e);
            V target = getEdgeTarget(e);

            if (!g.containsEdge(e)) {
                return false;
            }

            if (!g.getEdgeSource(e).equals(source) || !g.getEdgeTarget(e).equals(target)) {
                return false;
            }

            if (Math.abs(getEdgeWeight(e) - g.getEdgeWeight(e)) > 10e-7) {
                return false;
            }
        }

        return true;
    }

	
	
	
}
