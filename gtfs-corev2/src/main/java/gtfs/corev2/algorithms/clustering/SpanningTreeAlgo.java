package gtfs.corev2.algorithms.clustering;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

public interface SpanningTreeAlgo<E> {
	
	SpanningTree<E> getSpanningTree();
	
	interface SpanningTree<E> extends Iterable<E> {
		
		double getWeight();
		
		Set<E> getEdges();
		
		@Override
        default Iterator<E> iterator()
        {
            return getEdges().iterator();
        }
	}
	
	class SpanningTreeImpl<E> implements SpanningTree<E>, Serializable {
		
		private static final long serialVersionUID = 8762469646633L;

        private final double weight;
        private final Set<E> edges;

        public SpanningTreeImpl(Set<E> edges, double weight) {
            this.edges = edges;
            this.weight = weight;
        }

        @Override
        public double getWeight() {
            return weight;
        }

        @Override
        public Set<E> getEdges() {
            return edges;
        }

        @Override
        public String toString() {
            return "Spanning Tree [weight=" + weight + ", edges=" + edges + "]";
        }
	}
}
