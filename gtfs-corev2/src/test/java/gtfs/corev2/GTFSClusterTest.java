package gtfs.corev2;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.junit.Ignore;
import org.junit.Test;

import gtfs.corev2.algorithms.clustering.ClusteringAlgo.Clustering;
import gtfs.corev2.algorithms.shortestpath.BFS;
import gtfs.corev2.algorithms.clustering.GTFSClusterEdge;
import gtfs.corev2.algorithms.clustering.GTFSClusterVertex;
import gtfs.corev2.algorithms.clustering.KSpanningTreeClustering;

import gtfs.corev2.nio.GTFSGraphBuilder;
import gtfs.corev2.nio.util.Tuple;

public class GTFSClusterTest {

	@Test
	public void testClusterShortestPath() {
		Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();
		
		KSpanningTreeClustering c = new KSpanningTreeClustering(g, 40);
		Clustering<GTFSVertex> clusters = c.getClustering();
		
		//convert the clusters in disjoint-sets as a graph 
		Graph<GTFSClusterVertex, GTFSClusterEdge> gCluster = clusters.convertClustersAsGraph();
		
		//select two random GTFSVertex in two different GTFSClusterVertex
		Tuple<Tuple<GTFSClusterVertex, GTFSVertex>, Tuple<GTFSClusterVertex, GTFSVertex>> travel = randomPairAmongClusters(g, gCluster);
		
		// compute a path with BFS
		if (travel.first != null && travel.second != null) {
			System.out.println("starting node : "+travel.first.second.toString());
			System.out.println("starting cluster node : "+travel.first.first.toString());
			System.out.println("target node : "+travel.second.second.toString());
			System.out.println("target cluster node : "+travel.second.first.toString());
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////");
			System.out.println("///////////// starting BFS algorithm... ////////////");
			System.out.println("////////////////////////////////////////////////////");
			
			BFS<GTFSClusterVertex, GTFSClusterEdge> bfs = new BFS<GTFSClusterVertex, GTFSClusterEdge>(gCluster);
			List<GTFSClusterVertex> path = bfs.getPath(travel.first.first, travel.second.first).getVertexList();
			
			if (path.size() > 0) {
				for (GTFSClusterVertex v : path) {
					System.out.println(v.toString());
				}
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
			
		} else {
			System.out.println("Random vertex generation failed.");
		}
		
		//
		
	}
	
	private Tuple<Tuple<GTFSClusterVertex, GTFSVertex>, Tuple<GTFSClusterVertex, GTFSVertex>> randomPairAmongClusters(Graph<GTFSVertex, GTFSEdge> g, Graph<GTFSClusterVertex, GTFSClusterEdge> gCluster) {

		
		// 1) select two random cluster nodes
		int indexCluster = new Random().nextInt(gCluster.vertexSet().size());
		Iterator<GTFSClusterVertex> iterCluster = gCluster.vertexSet().iterator();
		for (int i = 0; i < indexCluster; i++) {
		    iterCluster.next();
		}
		GTFSClusterVertex startCluster = iterCluster.next();
		indexCluster = new Random().nextInt(gCluster.vertexSet().size());
		iterCluster = gCluster.vertexSet().iterator();
		for (int i = 0; i < indexCluster; i++) {
		    iterCluster.next();
		}
		GTFSClusterVertex endCluster = iterCluster.next();
		
		// 2) select two random GTFSVertex ; one in each cluster
		int index = new Random().nextInt(startCluster.getElements().size());
		Iterator<GTFSVertex> iterVertex = startCluster.getElements().iterator();
		for (int i = 0; i < index; i++) {
		    iterVertex.next();
		}
		GTFSVertex start = iterVertex.next();
		index = new Random().nextInt(endCluster.getElements().size());
		iterVertex = endCluster.getElements().iterator();
		for (int i = 0; i < index; i++) {
		    iterVertex.next();
		}
		GTFSVertex end = iterVertex.next();
		return 
			new Tuple<Tuple<GTFSClusterVertex, GTFSVertex>, Tuple<GTFSClusterVertex, GTFSVertex>>(
				new Tuple<GTFSClusterVertex, GTFSVertex>(startCluster, start), 
				new Tuple<GTFSClusterVertex, GTFSVertex>(endCluster, end)
			);
	}
}
