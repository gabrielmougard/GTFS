package gtfs.corev2;

import java.awt.Dimension;

import javax.swing.JFrame;


import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import gtfs.corev2.nio.GTFSGraphBuilder;

public class GraphVisualizer extends JFrame {
	
	private static final long serialVersionUID = -8123406571694511514L;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    private JGraphXAdapter<GTFSVertex, GTFSEdge> jgxAdapter;
    
    public static void main(String[] args)
    {
    	
    	
        GraphVisualizer frame = new GraphVisualizer();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 1000);
        frame.setVisible(true);
        

    }

    public GraphVisualizer() {
    	Graph<GTFSVertex, GTFSEdge> g = 
        		new GTFSGraphBuilder("mbta")
        		.localDataset()
        		.build();

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(true);
        getContentPane().add(component);



        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // center the circle
        int radius = 2;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
        layout.execute(jgxAdapter.getDefaultParent());
        
    }
    
}
