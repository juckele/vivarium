package com.johnuckele.vivarium.visualization.animation;

import com.johnuckele.vivarium.core.World;

public class Visualizer
{
    // Simulation variables
    private World _world;

    // Delegates
    private GraphicalDelegate _graphicalSystem;
    private SchedulingDelegate _schedulingSystem;

    private int _selectedCreatureID = 42;

    public Visualizer(World world, GraphicalDelegate graphicalSystem, SchedulingDelegate schedulingSystem)
    {
        _world = world;
        _graphicalSystem = graphicalSystem;
        _schedulingSystem = schedulingSystem;
    }

    public void renderWorld()
    {
        _graphicalSystem.render(_world, _selectedCreatureID);
    }

    public void tickWorld()
    {
        _world.tick();
    }

    public void start()
    {
        _schedulingSystem.start(this);
    }

    public World getWorld()
    {
        return _world;
    }

    public void setSelectedCreatureID(int selectedCreatureID)
    {
        _selectedCreatureID = selectedCreatureID;
    }
}
