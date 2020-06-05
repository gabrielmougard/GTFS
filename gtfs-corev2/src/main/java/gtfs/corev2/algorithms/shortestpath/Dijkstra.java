package gtfs.corev2.algorithms.shortestpath;

import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

import gtfs.corev2.nio.util.Tuple;

public class Dijkstra<V, E> extends BaseShortestPath<V, E>{
	
	private final double radius;
    private final Supplier<AddressableHeap<Double, Tuple<V, E>>> heapSupplier;
    
    public Dijkstra(Graph<V, E> graph) {
    	super(graph);
    	this.radius = Double.POSITIVE_INFINITY;
    	this.heapSupplier = PairingHeap::new;
        
    }
    
    @Override
    public GTFSPath<V, E> getPath(V source, V target) {

        DijkstraIterator<V, E> it = new DijkstraIterator<>(graph, source, radius, heapSupplier);

        while (it.hasNext()) {
            V vertex = it.next();
            if (vertex.equals(target)) {
                break;
            }
        }

        return it.getPaths().getPath(target);
    }
    
    @Override
    public Paths<V, E> getPaths(V source) {
    	
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }

        DijkstraIterator<V, E> it = new DijkstraIterator<>(graph, source, radius, heapSupplier);

        while (it.hasNext()) {
            it.next();
        }

        return it.getPaths();
    }

}
