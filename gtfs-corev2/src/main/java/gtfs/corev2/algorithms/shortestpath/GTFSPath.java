package gtfs.corev2.algorithms.shortestpath;

import java.util.*;

import org.jgrapht.Graph;

public interface GTFSPath<V, E> {

	Graph<V, E> getGraph();

    /**
     * start vertex in path
     */
    V getStartVertex();

    /**
     * last vertex in path
     */
    V getEndVertex();
    
    /**
     * Returns the path as a list of edges. 
     */
    default List<E> getEdgeList() {
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
        System.out.println("return edgeList");
        return edgeList;
    }

    /**
     * Returns the path as a list of vertices.
     */
    default List<V> getVertexList() {
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
        	//get opposite vertex
        	
        	V s = g.getEdgeSource(e);
            V t = g.getEdgeTarget(e);
            if (v.equals(s)) {
                v = t;
            } else if (v.equals(t)) {
                v = s;
            }  
        	
        	
            list.add(v);
        }
        System.out.println("return vertexList");
        return list;
    }

    /**
     * Returns the weight assigned to the path.
     */
    double getWeight();

    /**
     * Returns the length of the path : nb of edges
     */
    default int getLength() {
        return getEdgeList().size();
    }
}
