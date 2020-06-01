package gtfs.corev2;

import java.util.List;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BFSShortestPath;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import gtfs.corev2.nio.util.Distance;
import gtfs.corev2.nio.util.Tuple;

public class BFSFrame extends JFrame{
	public BFSFrame(Graph<GTFSVertex, GTFSEdge> g, Tuple<GTFSVertex, GTFSVertex> travel) {
		super("Path Visualization of BFS Shortest Path");
		
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
				System.out.println("Path found ! Size : "+path.size());
			} else {
				System.out.println("No path found !");
			}
			
			Tuple<mxGraph, Double> mxTravel = computeMxGraph(path);
			mxGraph mxPath = mxTravel.first;
			mxGraphComponent graphComponent = new mxGraphComponent(mxPath);
	
		    getContentPane().add(graphComponent);
		    
		    System.out.println("(BFSShortestPath) La distance totale est : "+mxTravel.second.toString()+" km");
		} else {
			System.out.println("Random vertex generation failed.");
		}
	
	}
	
	private Tuple<mxGraph, Double> computeMxGraph(List<GTFSVertex> path) {
		mxGraph mxPath = new mxGraph();
		
		int offsetX = 100;
		int offsetY = 100;
		int widthCell = 100;
		int heightCell = 60;
		int count = 0;
		int row = 0;
		
		Double lat;
		Double lon;
		Double totalDist = 0.0;
		
		Object parent = mxPath.getDefaultParent();
	    mxPath.getModel().beginUpdate();
	    try {
	    	Object v1 = mxPath.insertVertex(parent, null, path.get(0).toString(), offsetX, offsetY, widthCell, heightCell);
	    	count++;
	    	lat = path.get(0).getLat();
	    	lon = path.get(0).getLon();
	    	
	    	for (int i = 1; i < path.size(); i++) {
	    		
	    		int newOffsetX = updateOffsetX(offsetX, row, count, widthCell);
	    		int newOffsetY = updateOffsetY(offsetY, count, heightCell);
	    		
	    		Object v2 = mxPath.insertVertex(parent, null, path.get(i).toString(), newOffsetX, newOffsetY, widthCell, heightCell);
	    		
	    		Double d = Distance.latlon(lat, lon, path.get(i).getLat(), path.get(i).getLon());
	    		totalDist += d;
	    		mxPath.insertEdge(parent, null, "dist = "+d.toString()+" km", v1, v2);
	    		
	    		v1 = v2;
	    		offsetX = newOffsetX;
	    		offsetY = newOffsetY;
				count++;
				if (count % 10 == 0) { //print ten nodes per row
					row++;
				}
				lat = path.get(i).getLat();
				lon = path.get(i).getLon();
	    	}
	    		    	
	    } finally {
	    	mxPath.getModel().endUpdate();
	    }
		
		return new Tuple<mxGraph, Double>(mxPath, totalDist);
		
	}
	
	private int updateOffsetX(int offsetX, int row, int count, int widthCell) {
		if (row % 2 == 0) {
			if (count % 10 == 0) { //if we are at the end of a row, offsetX does not change
				return offsetX;
			}
			//we will increase offsetX
			return offsetX + 150 + widthCell; //50 is the padding
		} else {
			//we will decrease offsetX
			if (count % 10 == 0) { //if we are at the end of a row, offsetX does not change
				return offsetX;
			} else {
				return offsetX - (150+widthCell); 
			}
		}
	}
	
	private int updateOffsetY(int offsetY, int count, int heightCell) {
		if (count % 10 == 0) { //if we are at the end of a row
			return offsetY + 100 + heightCell;
			
		} else {
			return offsetY;
		}
	}
}
