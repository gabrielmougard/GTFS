package gtfs.corev2;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import gtfs.corev2.nio.GTFSGraphBuilder;
import gtfs.corev2.nio.util.Tuple;

public class PathVisualizer extends JFrame {

	private static final long serialVersionUID = -8123406566664511514L;
    
	public static void main(String[] args) {
    	
    	
		PathVisualizer frame = new PathVisualizer();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 1000);
        frame.setVisible(true);
        

    }
	
	public PathVisualizer() {
		super("Path Visualization of BFS Shortest Path");
    	Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();

        
    	//BFS
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
			
			mxGraph mxPath = computeMxGraph(path);
			mxGraphComponent graphComponent = new mxGraphComponent(mxPath);
		    getContentPane().add(graphComponent);
			
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
	
	private mxGraph computeMxGraph(List<GTFSVertex> path) {
		mxGraph mxPath = new mxGraph();
		
		int offsetX = 100;
		int offsetY = 100;
		int widthCell = 100;
		int heightCell = 60;
		int count = 0;
		int row = 0;
		
		Object parent = mxPath.getDefaultParent();
	    mxPath.getModel().beginUpdate();
	    try {
	    	Object v1 = mxPath.insertVertex(parent, null, path.get(0).toString(), offsetX, offsetY, widthCell, heightCell);
	    	count++;
	    	
	    	for (int i = 1; i < path.size(); i++) {
	    		
	    		int newOffsetX = updateOffsetX(offsetX, row, count, widthCell);
	    		int newOffsetY = updateOffsetY(offsetY, count, heightCell);
	    		
	    		Object v2 = mxPath.insertVertex(parent, null, path.get(i).toString(), newOffsetX, newOffsetY, widthCell, heightCell);
	    		
	    		
	    		mxPath.insertEdge(parent, null, "d = ??? km", v1, v2);
	    		
	    		v1 = v2;
	    		offsetX = newOffsetX;
	    		offsetY = newOffsetY;
				count++;
				if (count % 10 == 0) { //print ten nodes per row
					row++;
				}
	    	}
	    	
	    } finally {
	    	mxPath.getModel().endUpdate();
	    }
		
		return mxPath;
		
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
