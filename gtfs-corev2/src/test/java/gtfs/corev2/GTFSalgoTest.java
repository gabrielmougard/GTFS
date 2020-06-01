package gtfs.corev2;

import org.junit.Ignore;
import org.junit.Test;

import gtfs.corev2.algorithms.shortestpath.BFS;
//import gtfs.corev2.algorithms.shortestpath.BFSShortestPath;
import gtfs.corev2.nio.GTFSGraphBuilder;
import gtfs.corev2.nio.GraphSerializerBuilder;
import gtfs.corev2.nio.GraphSerializerBuilder.GraphSerializer;
import gtfs.corev2.nio.util.Tuple;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class GTFSalgoTest {

	
	@Test
	public void testBFS_GTFS() throws Exception {
		Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();
		// lets choose two random vertices for the experiment.
		Tuple<GTFSVertex, GTFSVertex> travel = randomPair(g);
		// jgraphT algo
		if (travel.first != null && travel.second != null) {
			System.out.println("starting node : "+travel.first.toString());
			System.out.println("target node : "+travel.second.toString());
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////");
			System.out.println("///////////// starting BFS algorithm... ////////////");
			System.out.println("////////////////////////////////////////////////////");
			//List<GTFSVertex> path = new BFSShortestPath(g, travel.first, travel.second).getPath();
			BFSShortestPath<GTFSVertex, GTFSEdge> bfs =
		            new BFSShortestPath<>(g);
			List<GTFSVertex> path = bfs.getPath(travel.first, travel.second).getVertexList();
			
			if (path.size() > 0) {
				for (GTFSVertex v : path) {
					System.out.println(v.toString());
				}
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
		} else {
			System.out.println("Random vertex generation failed.");
		}
		//custom algo
		if (travel.first != null && travel.second != null) {
			System.out.println("starting node : "+travel.first.toString());
			System.out.println("target node : "+travel.second.toString());
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////");
			System.out.println("///////////// starting BFS algorithm... ////////////");
			System.out.println("////////////////////////////////////////////////////");
			List<GTFSVertex> path = new BFS(g, travel.first, travel.second).getPath();
			
			if (path.size() > 0) {
				for (GTFSVertex v : path) {
					System.out.println(v.toString());
				}
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
		} else {
			System.out.println("Random vertex generation failed.");
		}
		
	}
	
	@Ignore
	@Test
	public void testDijkstra_GTFS() {
		Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();
		
		// lets choose two random vertices for the experiment.
		Tuple<GTFSVertex, GTFSVertex> travel = randomPair(g);
		//
		if (travel.first != null && travel.second != null) {
			System.out.println("starting node : "+travel.first.toString());
			System.out.println("target node : "+travel.second.toString());
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////");
			System.out.println("///////////// starting DIJKSTRA algorithm... ////////////");
			System.out.println("////////////////////////////////////////////////////");
			DijkstraShortestPath<GTFSVertex, GTFSEdge> dijkstra = 
					new DijkstraShortestPath<>(g);
			List<GTFSVertex> path = dijkstra.getPath(travel.first, travel.second).getVertexList();
			if (path.size() > 0) {
				for (GTFSVertex v : path) {
					System.out.println(v.toString());
				}
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
		} else {
			System.out.println("Random vertex generation failed.");
		}
		
	}
	
	@Ignore
	@Test
	public void testBFSvsBFSserialized() {
		Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();
		
		//serialize it 
		GraphSerializer gs = 
	        	new GraphSerializerBuilder("mbta")
	        	.localSerializer()
	        	.build();
	        
	    gs.serialize(g);
	    
	    //unserialize it
	    GraphSerializer gi =
	        	new GraphSerializerBuilder("mbta")
	        	.localSerializer()
	        	.build();
	        
	    Graph<GTFSVertex, GTFSEdge> g2 = gi.unserialize();
	    
		// lets choose two random vertices for the experiment (data integrity guaranteed for vertices after serialization).
		Tuple<GTFSVertex, GTFSVertex> travel = randomPair(g);
		//
		//for g
		if (travel.first != null && travel.second != null) {
			System.out.println("starting node : "+travel.first.toString());
			System.out.println("target node : "+travel.second.toString());
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////");
			System.out.println("///////////// starting BFS algorithm... ////////////");
			System.out.println("////////////////////////////////////////////////////");
			//List<GTFSVertex> path = new BFSShortestPath(g, travel.first, travel.second).getPath();
			BFSShortestPath<GTFSVertex, GTFSEdge> bfs =
		            new BFSShortestPath<>(g);
			List<GTFSVertex> path = bfs.getPath(travel.first, travel.second).getVertexList();
			
			if (path.size() > 0) {
				for (GTFSVertex v : path) {
					System.out.println(v.toString());
				}
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
		} else {
			System.out.println("Random vertex generation failed.");
		}
	    //for g2
		if (travel.first != null && travel.second != null) {
			System.out.println("starting node : "+travel.first.toString());
			System.out.println("target node : "+travel.second.toString());
			System.out.println("");
			System.out.println("////////////////////////////////////////////////////");
			System.out.println("///////////// starting BFS algorithm... ////////////");
			System.out.println("////////////////////////////////////////////////////");
			//List<GTFSVertex> path = new BFSShortestPath(g, travel.first, travel.second).getPath();
			BFSShortestPath<GTFSVertex, GTFSEdge> bfs =
		            new BFSShortestPath<>(g2);
			List<GTFSVertex> path = bfs.getPath(travel.first, travel.second).getVertexList();
			
			if (path.size() > 0) {
				for (GTFSVertex v : path) {
					System.out.println(v.toString());
				}
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
		} else {
			System.out.println("Random vertex generation failed.");
		}
	    
	    
	}
	
	private Tuple<GTFSVertex, GTFSVertex> randomPair(Graph<GTFSVertex, GTFSEdge> g) {

		int index = new Random().nextInt(g.vertexSet().size());
		Iterator<GTFSVertex> iter = g.vertexSet().iterator();
		for (int i = 0; i < index; i++) {
		    iter.next();
		}
		GTFSVertex start = iter.next();
		index = new Random().nextInt(g.vertexSet().size());
		iter = g.vertexSet().iterator();
		for (int i = 0; i < index; i++) {
		    iter.next();
		}
		GTFSVertex end = iter.next();
		return new Tuple<GTFSVertex, GTFSVertex>(start, end);
	}
}
