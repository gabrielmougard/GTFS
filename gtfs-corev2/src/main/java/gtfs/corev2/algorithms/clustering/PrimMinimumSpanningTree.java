package gtfs.corev2.algorithms.clustering;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;

import org.jheaps.AddressableHeap;
import org.jheaps.tree.FibonacciHeap; //an efficient fibonacci heap implementation used for MST

import gtfs.corev2.algorithms.clustering.util.CollectionUtil;
import gtfs.corev2.algorithms.clustering.util.VertexToIntegerMapping;

public class PrimMinimumSpanningTree<V, E> implements SpanningTreeAlgo<E> {
	
	private final Graph<V, E> g;
	
	public PrimMinimumSpanningTree(Graph<V, E> graph) {
        this.g = graph;
    }
	
	public SpanningTree<E> getSpanningTree() {
		
		Set<E> minimumSpanningTreeEdgeSet =
	            CollectionUtil.
	            newHashSetWithExpectedSize(g.vertexSet().size());
	    double spanningTreeWeight = 0d;
	    
	    final int N = this.g.vertexSet().size();
	    
	    VertexToIntegerMapping<V> vertexToIntegerMapping = new VertexToIntegerMapping<>(this.g.vertexSet());
	    Map<V, Integer> vertexMap = vertexToIntegerMapping.getVertexMap();
        List<V> indexList = vertexToIntegerMapping.getIndexList();
        
        VertexInfo[] vertices = (VertexInfo[]) Array.newInstance(VertexInfo.class, N);
        AddressableHeap.Handle<Double, VertexInfo>[] fibNodes =
            (AddressableHeap.Handle<Double, VertexInfo>[]) 
            	Array
                .newInstance(AddressableHeap.Handle.class, N);
        
        AddressableHeap<Double, VertexInfo> fibonacciHeap = new FibonacciHeap<>();

        for (int i = 0; i < N; i++) {
            vertices[i] = new VertexInfo();
            vertices[i].id = i;
            vertices[i].distance = Double.MAX_VALUE;
            fibNodes[i] = fibonacciHeap.insert(vertices[i].distance, vertices[i]);
        }
        
        while (!fibonacciHeap.isEmpty()) {
            AddressableHeap.Handle<Double, VertexInfo> fibNode = fibonacciHeap.deleteMin();
            VertexInfo vertexInfo = fibNode.getValue();

            V p = indexList.get(vertexInfo.id);
            vertexInfo.spanned = true;

            // Add the edge from its parent to the spanning tree (if it exists)
            if (vertexInfo.edgeFromParent != null) {
                minimumSpanningTreeEdgeSet.add(vertexInfo.edgeFromParent);
                spanningTreeWeight += this.g.getEdgeWeight(vertexInfo.edgeFromParent);
            }

            // update all (unspanned) neighbors of p
            for (E e : this.g.edgesOf(p)) {
                V q = getOppositeVertex(e, p);
                int id = vertexMap.get(q);

                // if the vertex is not explored and we found a better edge, then update the info
                if (!vertices[id].spanned) {
                    double cost = this.g.getEdgeWeight(e);

                    if (cost < vertices[id].distance) {
                        vertices[id].distance = cost;
                        vertices[id].edgeFromParent = e;
                        fibNodes[id].decreaseKey(cost);
                    }
                }
            }
        }
        
        return new SpanningTreeImpl<>(minimumSpanningTreeEdgeSet, spanningTreeWeight);

	}
	
	private V getOppositeVertex(E e, V p) {
		V source = this.g.getEdgeSource(e);
        V target = this.g.getEdgeTarget(e);
        if (p.equals(source)) {
            return target;
        } else if (p.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException("no such vertex: " + p.toString());
        }
	}
	
	private class VertexInfo {
        public int id;
        public boolean spanned;
        public double distance;
        public E edgeFromParent;
    }
}
