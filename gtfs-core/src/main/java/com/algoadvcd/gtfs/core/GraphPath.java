package com.algoadvcd.gtfs.core;

import java.util.*;

public interface GraphPath<V, E> {
	
	Graph<V, E> getGraph();

    V getStartVertex();

    V getEndVertex();

    /**
     * Returns the edges making up the path. The first edge in this path is incident to the start
     * vertex. The last edge is incident to the end vertex. 
     *
     */
    default List<E> getEdgeList()
    {
        List<V> vertexList = this.getVertexList();
        if (vertexList.size() < 2)
            return Collections.emptyList();

        Graph<V, E> g = this.getGraph();
        List<E> edgeList = new ArrayList<>();
        Iterator<V> vertexIterator = vertexList.iterator();
        V u = vertexIterator.next();
        while (vertexIterator.hasNext()) {
            V v = vertexIterator.next();
            edgeList.add(g.getEdge(u, v));
            u = v;
        }
        return edgeList;
    }

    /**
     * Returns the path as a sequence of vertices.
     */
    default List<V> getVertexList()
    {
        List<E> edgeList = this.getEdgeList();

        if (edgeList.isEmpty()) {
            V startVertex = getStartVertex();
            if (startVertex != null && startVertex.equals(getEndVertex())) {
                return Collections.singletonList(startVertex);
            } else {
                return Collections.emptyList();
            }
        }

        Graph<V, E> g = this.getGraph();
        List<V> list = new ArrayList<>();
        V v = this.getStartVertex();
        list.add(v);
        for (E e : edgeList) {
            v = Graphs.getOppositeVertex(g, e, v);
            list.add(v);
        }
        return list;
    }

    /**
     * Returns the weight assigned to the path. Typically, this will be the sum of the weights of
     * the edge list entries 
     */
    double getWeight();

    /**
     * Returns the length of the path, measured in the number of edges.
     */
    default int getLength()
    {
        return getEdgeList().size();
    }
}
