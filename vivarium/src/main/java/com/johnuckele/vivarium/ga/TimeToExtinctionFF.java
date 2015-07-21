package com.johnuckele.vivarium.ga;

import java.util.ArrayList;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;

public class TimeToExtinctionFF extends FitnessFunction
{
    private int            _initialPopulation;
    private int            _worldSize;
    private WorldVariables _variables;

    public TimeToExtinctionFF(int worldSize, WorldVariables variables, int initialPopulation)
    {
        this._worldSize = worldSize;
        this._variables = variables;
        this._initialPopulation = initialPopulation;
    }

    @Override
    public double evaluate(Creature c)
    {
        // Build world
        WorldVariables instanceVariables = _variables.clone();
        ArrayList<Species> instanceSpecies = instanceVariables.getSpecies();
        World w = new World(_worldSize, instanceVariables);
        for (Species s : instanceSpecies)
        {
            s.setMutationEnabled(false);
            for (int i = 0; i < _initialPopulation; i++)
            {
                w.addImmigrant(s);
            }
        }

        // Run simulation
        double tenLifespans = c.getSpecies().getMaximumAge() * 2;
        for (int i = 0; i < tenLifespans; i++)
        {
            int count = w.getCount(WorldObject.CREATURE);
            if (count == 0)
            {
                return i / tenLifespans;
            }
            w.tick();
        }
        return 1.0;
    }
}
