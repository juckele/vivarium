package com.johnuckele.vivarium.visualization.text;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.visualization.RenderCode;

@SuppressWarnings("serial")
public class WorldViewer extends JFrame
{
    public static final int OVERVIEW_ONLY = 1;
    public static final int RAT_LIST_ONLY = 2;

    private World             _w;
    private ArrayList<String> _overviewRenders = new ArrayList<String>(50000);
    private ArrayList<String> _ratListRenders  = new ArrayList<String>(50000);
    private int               _renderIndex     = 0;

    private JTextArea                  _overviewRender;
    private JTextArea                  _ratListRender;
    private WorldViewerFrameController _frameController;

    public WorldViewer(World w)
    {
        _w = w;
        _overviewRenders.add(_w.render(RenderCode.WORLD_MAP) + "\n" + _w.render(RenderCode.BRAIN_WEIGHTS));
        _ratListRenders.add(_w.render(RenderCode.LIVE_CREATURE_LIST));

        _overviewRender = new JTextArea(_overviewRenders.get(_renderIndex));
        _ratListRender = new JTextArea(_ratListRenders.get(_renderIndex));
        _frameController = new WorldViewerFrameController(this, 0);

        this.add(_overviewRender);
        this.add(_ratListRender);
        this.add(_frameController);

        _overviewRender.setBounds(0, 35, 900, 965);
        _overviewRender.setTabSize(5);
        _ratListRender.setBounds(900, 35, 800, 965);

        this.setSize(1700, 1000);
        this.setVisible(true);
    }

    public void runTicks(int ticks)
    {
        for (int tick = 1; tick <= ticks; tick++)
        {
            _w.tick();
        }
    }

    public void runAndRenderTicks(int ticks, int renderEvery)
    {
        // long startTime = System.currentTimeMillis();
        for (int tick = 1; tick <= ticks; tick++)
        {
            _w.tick();
            if (tick % renderEvery == 0)
            {
                _overviewRenders.add(_w.render(RenderCode.WORLD_MAP) + "\n" + _w.render(RenderCode.BRAIN_WEIGHTS));
                _ratListRenders.add(_w.render(RenderCode.LIVE_CREATURE_LIST));
                System.out.println("Population " + _w.getCount(EntityType.CREATURE));
            }
        }
    }

    public void setRenderIndex(int index)
    {
        index = Math.max(this.minIndex(), index);
        index = Math.min(this.maxIndex(), index);
        _renderIndex = index;
        _overviewRender.setText(_overviewRenders.get(_renderIndex));
        _ratListRender.setText(_ratListRenders.get(_renderIndex));
        _frameController.setRenderIndex(index);
    }

    public int minIndex()
    {
        return (0);
    }

    public int maxIndex()
    {
        return (_overviewRenders.size() - 1);
    }

    public static void main(String[] args)
    {
        // Set up
        int worldDimensions = 25;
        System.out.println("Creating world... " + worldDimensions + " x " + worldDimensions);
        LinkedList<Species> species = new LinkedList<Species>();

        // Build 1 species
        Species species1 = Species.makeDefault();
        species1.setMutationRateExponent(-9);
        species1.setCreatureMemoryUnitCount(1);
        species1.setCreatureSoundChannelCount(1);
        species.add(species1);

        // Build another
        Species species2 = Species.makeDefault();
        species2.setBrainType(BrainType.RANDOM);
        species.add(species2);

        // Construct the world proper
        Blueprint blueprint = Blueprint.makeWithSizeAndSpecies(worldDimensions, species);
        World w = new World(blueprint);
        System.out.println("Created world... " + worldDimensions + " x " + worldDimensions);

        // Run simulation
        WorldViewer wh = new WorldViewer(w);
        wh.runAndRenderTicks(200000, 2000);
        // wh.runAndRenderTicks(20000, 1);

        System.out.println("Finished simulations");
    }
}
