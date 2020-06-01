package gtfs.corev2;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import gtfs.corev2.nio.GTFSGraphBuilder;
import gtfs.corev2.nio.util.Distance;
import gtfs.corev2.nio.util.Tuple;

public class PathVisualizer extends JFrame {

	private static final long serialVersionUID = -8123406566664511514L;


	public static void main(String[] args) {
    	
		Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();
		
		
		// lets choose two random vertices for the experiment.
		Tuple<GTFSVertex, GTFSVertex> travel = randomPair(g);
		
		BFSFrame bfsFrame = new BFSFrame(g, travel);
		DijkstraFrame dijkstraFrame = new DijkstraFrame(g, travel);
		
		bfsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bfsFrame.setSize(1900, 1000);
		bfsFrame.setVisible(true);

		dijkstraFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dijkstraFrame.setSize(1900, 1000);
		dijkstraFrame.setVisible(true);
        

    }
	
	private static Tuple<GTFSVertex, GTFSVertex> randomPair(Graph<GTFSVertex, GTFSEdge> g) {

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
