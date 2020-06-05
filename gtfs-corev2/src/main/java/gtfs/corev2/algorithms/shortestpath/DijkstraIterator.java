package gtfs.corev2.algorithms.shortestpath;

import java.util.*;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import org.jheaps.AddressableHeap;

import gtfs.corev2.algorithms.shortestpath.ShortestPath.Paths;
import gtfs.corev2.nio.util.Tuple;

public class DijkstraIterator<V, E> implements Iterator<V>{
	
	private final Graph<V, E> graph;
    private final V source;
    private final double radius;
    private final Map<V, AddressableHeap.Handle<Double, Tuple<V, E>>> seen;
    private AddressableHeap<Double, Tuple<V, E>> heap;
  
    
    public DijkstraIterator(Graph<V, E> graph, V source, double radius, Supplier<AddressableHeap<Double, Tuple<V, E>>> heapSupplier) {
            
    	this.graph = graph;
        this.source = source;
        this.radius = radius;
        this.seen = new HashMap<>();
        this.heap = heapSupplier.get();
        // initialize with source vertex
        updateDistance(source, null, 0d);
    }
    
    @Override
    public boolean hasNext() {
        
    	if (heap.isEmpty()) {
            return false;
        }
        AddressableHeap.Handle<Double, Tuple<V, E>> vNode = heap.findMin();
        double vDistance = vNode.getKey();
        if (radius < vDistance) {
            heap.clear();
            return false;
        }
        return true;
    }
    
    @Override
    public V next() {
        
    	if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // settle next node
        AddressableHeap.Handle<Double, Tuple<V, E>> vNode = heap.deleteMin();
        V v = vNode.getValue().first;
        double vDistance = vNode.getKey();

        // relax edges
        for (E e : graph.outgoingEdgesOf(v)) {
            V u = Graphs.getOppositeVertex(graph, e, v);
            double eWeight = graph.getEdgeWeight(e);
            if (eWeight < 0.0) {
                throw new IllegalArgumentException("Negative edge weight not allowed");
            }
            updateDistance(u, e, vDistance + eWeight);
        }

        return v;
    }
    
    public Paths<V, E> getPaths() {
        return new TreeGTFSPaths<>(graph, source, getDistanceAndPredecessorMap());
    }
    
    public Map<V, Tuple<Double, E>> getDistanceAndPredecessorMap() {
    	
        Map<V, Tuple<Double, E>> distanceAndPredecessorMap = new HashMap<>();

        for (AddressableHeap.Handle<Double, Tuple<V, E>> vNode : seen.values()) {
            double vDistance = vNode.getKey();
            if (radius < vDistance) {
                continue;
            }
            V v = vNode.getValue().first;
            distanceAndPredecessorMap.put(v, new Tuple(vDistance, vNode.getValue().second));
        }

        return distanceAndPredecessorMap;
    }

    private void updateDistance(V v, E e, double distance) {
    	
        AddressableHeap.Handle<Double, Tuple<V, E>> node = seen.get(v);
        
        if (node == null) {
            node = heap.insert(distance, new Tuple(v, e));
            seen.put(v, node);
        } else if (distance < node.getKey()) {
            node.decreaseKey(distance);
            node.setValue(new Tuple(node.getValue().first, e));
        }
        
    }

    
    
}
