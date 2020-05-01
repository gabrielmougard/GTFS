package com.algoadvcd.gtfs.core;

import java.util.*;
import java.util.function.*;

public interface Graph<V, E>
{

    Set<E> getAllEdges(V sourceVertex, V targetVertex);
    
    E getEdge(V sourceVertex, V targetVertex);
    
    Supplier<V> getVertexSupplier();
    
    Supplier<E> getEdgeSupplier();
    
    E addEdge(V sourceVertex, V targetVertex);
    
    boolean addEdge(V sourceVertex, V targetVertex, E e);
    
    V addVertex();
    
    boolean addVertex(V v);

    boolean containsEdge(V sourceVertex, V targetVertex);

    boolean containsEdge(E e);
    
    boolean containsVertex(V v);

    Set<E> edgeSet();

    int degreeOf(V vertex);

    Set<E> edgesOf(V vertex);

    int inDegreeOf(V vertex);

    Set<E> incomingEdgesOf(V vertex);

    int outDegreeOf(V vertex);

    Set<E> outgoingEdgesOf(V vertex);

    boolean removeAllEdges(Collection<? extends E> edges);
  
    Set<E> removeAllEdges(V sourceVertex, V targetVertex);

    boolean removeAllVertices(Collection<? extends V> vertices);

    E removeEdge(V sourceVertex, V targetVertex);

    boolean removeEdge(E e);

    boolean removeVertex(V v);

    Set<V> vertexSet();

    V getEdgeSource(E e);

    V getEdgeTarget(E e);

    GraphType getType();

    double DEFAULT_EDGE_WEIGHT = 1.0;

    double getEdgeWeight(E e);

    void setEdgeWeight(E e, double weight);

    default void setEdgeWeight(V sourceVertex, V targetVertex, double weight)
    {
        this.setEdgeWeight(this.getEdge(sourceVertex, targetVertex), weight);
    }
}
