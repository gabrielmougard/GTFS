package gtfs.corev2.algorithms.shortestpath;

import org.jgrapht.Graph;

import gtfs.corev2.nio.util.Tuple;

import java.util.*;


public class BFS<V, E> extends BaseShortestPath<V, E> {

	public BFS(Graph<V, E> graph) {
		super(graph);
	}
	
	@Override
    public Paths<V, E> getPaths(V source) {
		
        Map<V, Tuple<Double, E>> distParentMap = new HashMap<>();
		distParentMap.put(source, new Tuple(0d, null));

        Deque<V> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            V v = queue.poll();
            for (E e : graph.outgoingEdgesOf(v)) {
            	
            	//get opposite vertex
                V u = null;
                V s = this.graph.getEdgeSource(e);
                V t = this.graph.getEdgeTarget(e);
                if (v.equals(s)) {
                    u = t;
                } else if (v.equals(t)) {
                    u = s;
                }
                
                if (!distParentMap.containsKey(u)) {
                    queue.add(u);
                    double newDist = distParentMap.get(v).first + 1.0;
                    distParentMap.put(u, new Tuple(newDist, e));
                }
            }
        }

        return new TreeGTFSPaths<>(graph, source, distParentMap);

    }
	
	@Override
    public GTFSPath<V, E> getPath(V source, V target) {

        return getPaths(source).getPath(target);
    }
	
}