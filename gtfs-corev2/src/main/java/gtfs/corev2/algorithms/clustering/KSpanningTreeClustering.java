package gtfs.corev2.algorithms.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.Graph;

import gtfs.corev2.GTFSEdge;
import gtfs.corev2.GTFSVertex;
import gtfs.corev2.algorithms.clustering.SpanningTreeAlgo.SpanningTree;

public class KSpanningTreeClustering implements ClusteringAlgo{
	
	private int k;
	private Graph<GTFSVertex, GTFSEdge> graph;
	/*
	 * g is the graph
	 * k is the number of clusters
	 */
	public KSpanningTreeClustering(Graph<GTFSVertex, GTFSEdge> g, int k) {
		this.graph = g;
		if (k < 1 || k > g.vertexSet().size()) {
            throw new IllegalArgumentException("Illegal number of clusters");
        }
		this.k = k;
	}
	
	@Override
	public Clustering<GTFSVertex> getClustering() {
		/*
		 * Compute a Minimum Spanning Tree
		 */
		SpanningTree<GTFSEdge> mst = new PrimMinimumSpanningTree<>(this.graph).getSpanningTree();
		
		/*
		 * Build the clusters using the MST edges
		 */
		UnionFind<GTFSVertex> forest = new UnionFind<>(this.graph.vertexSet());
		ArrayList<GTFSEdge> allEdges = new ArrayList<>(mst.getEdges());
		
		Collections.sort(allEdges);
		
		
		int edgeCount = 0;
		
		for (GTFSEdge edge : allEdges) {
			if (forest.numberOfSets() == k) {
				break;
			}
			edgeCount++;
			GTFSVertex source = this.graph.getEdgeSource(edge);
			GTFSVertex target = this.graph.getEdgeTarget(edge);
			if (forest.find(source).equals(forest.find(target))) {
				continue;
			}
			
			forest.union(source, target);
			
		}
		
		//now allEdges[edgeCount:] contain all the edges linking the different clusters
		List<GTFSEdge> clusterEdges = allEdges.subList(edgeCount, allEdges.size()-1);
		System.out.println("size of cluster Edges : "+clusterEdges.size());
		/*
		 * Transform and return result
		 */
		
		Map<GTFSVertex, Set<GTFSVertex>> clusterMap = new LinkedHashMap<>();
		for (GTFSVertex v : this.graph.vertexSet()) {
			GTFSVertex rv = forest.find(v); //representative vertex
			Set<GTFSVertex> cluster = clusterMap.get(rv);
			if (cluster == null) {
				cluster = new LinkedHashSet<>();
				clusterMap.put(rv, cluster);
			}
			cluster.add(v);
		}
		
		
		
		
		return new ClusteringImpl<>(clusterMap, clusterEdges);
		
	}
}
