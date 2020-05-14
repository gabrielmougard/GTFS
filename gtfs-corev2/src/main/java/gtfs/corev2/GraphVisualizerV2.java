package gtfs.corev2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

import gtfs.corev2.nio.GTFSGraphBuilder;
import gtfs.corev2.nio.Target;

/**
 *
 * @author Thanasis1101
 * @version 1.0
 */
public class GraphVisualizerV2 extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

    private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;
    
    //graph related
    private String pathToDataset;
    private Target target;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    private mxGraphComponent graphComponent;

    public GraphVisualizerV2(String pathToDataset, Target target) {

        this.pathToDataset = pathToDataset;
        this.target = target;
        
        initComponent();

    }

    private void initComponent() {
    	addGraphComponent();
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    
    private void addGraphComponent() {
    	Graph<GTFSVertex, GTFSEdge> g;
    	if (this.target == Target.LOCAL) {
    		g = 
    			new GTFSGraphBuilder(this.pathToDataset)
            	.localDataset()
            	.build();
    	} else {
    		g = 
            	new GTFSGraphBuilder(this.pathToDataset)
            	.remoteDataset()
            	.build();
    	}
    	
    	// create a visualization using JGraph, via an adapter
    	JGraphXAdapter<GTFSVertex, GTFSEdge> jgxAdapter = new JGraphXAdapter<>(g);
    	setPreferredSize(DEFAULT_SIZE);
    	graphComponent = new mxGraphComponent(jgxAdapter);
    	
    	graphComponent.setConnectable(false);
    	graphComponent.getGraph().setAllowDanglingEdges(true);
    	MouseWheelListener graphZoomListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO Auto-generated method stub
				
				mxGraphComponent c = (mxGraphComponent) e.getComponent();
				//mxGraph g = c.getGraph();
				//mxGraphView view = g.getView();
			    //double scale = view.getScale();
				if (e.getWheelRotation() < 0)
				{
					c.zoomIn();
				}
				else
				{
					c.zoomOut();
				}
				
			}
    		
    	};
		graphComponent.addMouseWheelListener(graphZoomListener);
        this.add(graphComponent);
        
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (zoomer) {
        	System.out.println("zoom : "+zoomFactor);
            graphComponent.zoom(zoomFactor);
            zoomer = false;
        }

        if (dragger) {
            AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            //g2.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragger = false;
            }

        }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        zoomer = true;

        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomFactor *= 1.1;
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoomFactor /= 1.1;
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;

        dragger = true;
        repaint();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    	System.out.println("coucou");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        released = false;
        startPoint = MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        released = true;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
