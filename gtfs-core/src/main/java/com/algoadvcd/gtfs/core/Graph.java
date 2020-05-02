package com.algoadvcd.gtfs.core;

import java.util.*;
import java.util.function.*;

public interface Graph<V> extends Iterable<V> {

	public Map<V, Set<V>> getVerticesMap();
	public int getNumVertices();
	public int getNumEdges();
	public int degree(V v);
	public void addEdge(V v, V w);
	public void addVertex(V v);
	public boolean hasEdge(V v, V w);
	public boolean hasVertex(V v);
	public Iterator<V> iterator();
	
}
