/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import io.vivarium.core.brain.Brain;
import io.vivarium.core.brain.RandomBrain;
import io.vivarium.visualization.animation.ThreadScheduler;
import io.vivarium.visualization.animation.Visualizer;

public class BrainViewerDemo extends JPanel implements GraphicalController
{
    private static JFrame _window;

    private static final long serialVersionUID = -3105685457075818705L;

    // Simulation variables
    private Visualizer _visualizer;

    // Animation variables
    private SwingGeometricGraphics _swingGraphics;
    private ThreadScheduler _threadScheduler;

    // UI variables
    private long _lastMouseEvent = System.currentTimeMillis();
    private boolean _cursorHidden = false;

    public BrainViewerDemo(Brain brain)
    {
        _swingGraphics = new SwingGeometricGraphics(this, this);
        _threadScheduler = new ThreadScheduler();
        // _visualizer = new Visualizer(brain, _swingGraphics, _threadScheduler);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Get the graphics for the graphical delegate
        Graphics2D g2 = (Graphics2D) g;
        _swingGraphics.setResources(g2);

        // Hand off to the graphical delegate to perform the render
        _swingGraphics.executeRender();
    }

    public static void main(String[] args)
    {
        Brain brain = new RandomBrain(4);

        // Create and show the window
        BrainViewerDemo bv = new BrainViewerDemo(brain);
        _window = new JFrame();
        _window.add(bv);
        _window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _window.validate();

        // Set everything to be visible
        _window.setSize(100, 100);
        _window.setVisible(true);
    }

    @Override
    public void onRender(GeometricGraphics graphics)
    {
        graphics.drawRectangle(10, 10, 30, 30);
    }
}
