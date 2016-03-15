package io.vivarium.visualization.animation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.visualization.util.Fullscreen;

public class AnimatedWorldViewer extends JPanel implements KeyListener, MouseListener, MouseMotionListener
{
    private static JFrame _window;

    private static final long serialVersionUID = -3105685457075818705L;

    // Simulation variables
    private Visualizer _visualizer;

    // Animation variables
    private SwingWorldGraphics _swingGraphics;
    private ThreadScheduler _threadScheduler;

    // UI variables
    private UIThread _uiThread;
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

    public AnimatedWorldViewer(World w)
    {
        _swingGraphics = new SwingWorldGraphics(this);
        _threadScheduler = new ThreadScheduler();
        _visualizer = new Visualizer(w, _swingGraphics, _threadScheduler);
        _uiThread = new UIThread();
    }

    public void startHelperThreads()
    {
        _visualizer.start();
        _uiThread.start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Get the graphics for the graphical delegate
        Graphics2D g2 = (Graphics2D) g;
        _swingGraphics.setResources(g2);

        // Hand off to the graphical delegate to perform the render
        _swingGraphics.renderFrame();
    }

    public static void main(String[] args)
    {
        // Set up
        int worldDimensions = 34;
        System.out.println("Creating world... " + worldDimensions + " x " + worldDimensions);
        ArrayList<CreatureBlueprint> species = new ArrayList<>();

        // Build 1 species
        CreatureBlueprint species1 = CreatureBlueprint.makeDefault();
        species1.setMutationRateExponent(-9);
        species1.setCreatureMemoryUnitCount(1);
        species1.setCreatureSoundChannelCount(1);
        species.add(species1);

        // Build another
        // Species species2 = Species.makeDefault();
        // species2.setProcessorType(ProcessorType.RANDOM);
        // species.add(species2);

        // Construct the world proper
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        blueprint.setSpecies(species);
        blueprint.setWidth(60);
        blueprint.setHeight(34);
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

        // Start everything
        wh.startHelperThreads();

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
        int blockX = arg0.getX() / SpriteRenderer.RENDER_PIXEL_BLOCK_SIZE;
        int blockY = arg0.getY() / SpriteRenderer.RENDER_PIXEL_BLOCK_SIZE;
        Creature c = _visualizer.getWorld().getCreature(blockY, blockX);
        int selectedCreatureID = c != null ? c.getID() : -1;
        _visualizer.setSelectedCreatureID(selectedCreatureID);
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
