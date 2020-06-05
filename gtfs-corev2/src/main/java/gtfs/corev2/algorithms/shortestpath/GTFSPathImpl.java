package gtfs.corev2.algorithms.shortestpath;

import java.io.*;
import java.util.*;

import org.jgrapht.Graph;

public class GTFSPathImpl<V, E> implements GTFSPath<V, E>, Serializable {

	private static final long serialVersionUID = 77629469664L;
    protected Graph<V, E> graph;
    protected List<V> vertexList;
    protected List<E> edgeList;
    protected V startVertex;
    protected V endVertex;
    protected double weight;
   
    public GTFSPathImpl(Graph<V, E> graph, V startVertex, V endVertex, List<V> vertexList, List<E> edgeList, double weight) {
            
            this.graph = graph;
            this.startVertex = startVertex;
            this.endVertex = endVertex;
            this.vertexList = vertexList;
            this.edgeList = edgeList;
            this.weight = weight;
    }

    @Override
    public Graph<V, E> getGraph() {
    	return graph;
    }
    
    @Override
    public V getStartVertex()
    {
        return startVertex;
    }

    @Override
    public V getEndVertex()
    {
        return endVertex;
    }

    @Override
    public List<E> getEdgeList()
    {
        return (edgeList != null ? edgeList : GTFSPath.super.getEdgeList());
    }

    @Override
    public List<V> getVertexList()
    {
    	return (vertexList != null ? vertexList : GTFSPath.super.getVertexList());
    }

    @Override
    public double getWeight()
    {
        return weight;
    }
    
    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    @Override
    public int getLength()
    {
        if (edgeList != null)
            return edgeList.size();
        else if (vertexList != null && !vertexList.isEmpty())
            return vertexList.size() - 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof GTFSPathImpl))
            return false;
        else if (this == o)
            return true;
        @SuppressWarnings("unchecked") 
        GTFSPathImpl<V, E> other = (GTFSPathImpl<V, E>) o;
        if (this.isEmpty() && other.isEmpty())
            return true;

        if (this.isEmpty())
            return false;

        if (!this.startVertex.equals(other.getStartVertex())
            || !this.endVertex.equals(other.getEndVertex()))
            return false;

        if (this.edgeList == null && !other.getGraph().getType().isAllowingMultipleEdges()) {
        	return this.vertexList.equals(other.getVertexList());
        } else {
        	return this.getEdgeList().equals(other.getEdgeList());
        }  
    }
    
    @Override
    public int hashCode()
    {
        int hashCode = 1;
        if (isEmpty())
            return hashCode;

        hashCode = 31 * hashCode + startVertex.hashCode();
        hashCode = 31 * hashCode + endVertex.hashCode();

        if (edgeList != null)
            return 31 * hashCode + edgeList.hashCode();
        else
            return 31 * hashCode + vertexList.hashCode();
    }

    
    public boolean isEmpty() {
        return startVertex == null;
    }
    
    public static <V, E> GTFSPathImpl<V, E> singletonWalk(Graph<V, E> graph, V v, double weight) {
        return new GTFSPathImpl<>(
            graph, v, v, Collections.singletonList(v), Collections.emptyList(), weight);
    }
    
}


