package gtfs.corev2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

import gtfs.corev2.nio.Target;

/**
 *
 * @author Thanasis1101
 * @version 1.0
 */
public class MainFrame extends JFrame {

    private GraphVisualizer graphPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setTitle("GTFS Graph Visualizer");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        graphPanel = new GraphVisualizer("GTFS Graph Visualizer","mbta", Target.LOCAL);
		graphPanel.setBounds(50, 50, width - 100, height - 240);
		graphPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		this.add(graphPanel);
		graphPanel.setVisible(true);
    }
}
