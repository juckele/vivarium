package com.johnuckele.vivarium.visualization.animation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.visualization.util.Fullscreen;

public class AnimatedWorldViewer extends JPanel implements KeyListener, MouseListener, MouseMotionListener
{
    private static JFrame _window;

    private static final long serialVersionUID = -3105685457075818705L;

    // Simulation variables
    private World _world;

    // Animation variables

    // UI variables
    private long _lastMouseEvent = System.currentTimeMillis();
    private boolean _cursorHidden = false;

    private class UIThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                long now = System.currentTimeMillis();
                try
                {
                    if (!_cursorHidden && now - 10000 > _lastMouseEvent)
                    {
                        Fullscreen.setCursorVisible(_window, false);
                        _cursorHidden = true;
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AnimationThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    repaint();
                    Thread.sleep(25);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SimulationThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    System.out.println("Ticking the world!");
                    _world.tick();
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public AnimatedWorldViewer(World w)
    {
        _world = w;
        this.setVisible(true);
        this.setSize(800, 600);
        new AnimationThread().start();
        new SimulationThread().start();
        new UIThread().start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // g2.rotate(Math.toRadians(0));
        WorldRenderer.terrainRender(g2, _world, this);
        WorldRenderer.actorRender(g2, _world, this, (int) (System.currentTimeMillis() % 1000));
        // renderCreatures(g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine(0, 0, 800, (int) ((System.currentTimeMillis() / 40) % 400));

        Toolkit.getDefaultToolkit().sync();
    }

    public static void main(String[] args)
    {
        // Set up
        int worldDimensions = 40;
        System.out.println("Creating world... " + worldDimensions + " x " + worldDimensions);
        LinkedList<Species> species = new LinkedList<Species>();

        // Build 1 species
        Species species1 = Species.makeDefault();
        species1.setMutationRateExponent(-9);
        species1.setCreatureMemoryUnitCount(1);
        species1.setCreatureSoundChannelCount(1);
        species.add(species1);

        // Build another
        // Species species2 = Species.makeDefault();
        // species2.setBrainType(BrainType.RANDOM);
        // species.add(species2);

        // Construct the world proper
        Blueprint blueprint = Blueprint.makeWithSizeAndSpecies(worldDimensions, species);
        World w = new World(blueprint);
        System.out.println("Created world... " + worldDimensions + " x " + worldDimensions);

        // Create and show the window
        AnimatedWorldViewer wh = new AnimatedWorldViewer(w);
        _window = new JFrame();
        _window.add(wh);
        _window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _window.validate();

        // Add listeners
        _window.addKeyListener(wh);
        _window.addMouseListener(wh);
        _window.addMouseMotionListener(wh);

        // Set everything to be visible
        _window.setVisible(true);
        Fullscreen.setFullScreenWindow(_window, true);
        // Fullscreen.hideCursor(window);

    }

    // UI Event Listeners

    @Override
    public void keyPressed(KeyEvent event)
    {
        if (event.getKeyChar() == 'q')
        {
            System.exit(0);
        }
        if (event.getKeyChar() == 'w')
        {
            Fullscreen.setCursorVisible(_window, false);
            _cursorHidden = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
    }

    @Override
    public void keyTyped(KeyEvent event)
    {
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    @Override
    public void mousePressed(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    @Override
    public void mouseDragged(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    @Override
    public void mouseMoved(MouseEvent arg0)
    {
        updateMouseCursor();
    }

    private void updateMouseCursor()
    {
        _lastMouseEvent = System.currentTimeMillis();
        if (_cursorHidden)
        {
            Fullscreen.setCursorVisible(_window, true);
            _cursorHidden = false;
        }
    }
}