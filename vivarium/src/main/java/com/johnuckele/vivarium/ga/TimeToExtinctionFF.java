package com.johnuckele.vivarium.ga;

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
        assert (instanceVariables.getSpecies().size() == 1);
        Species instanceSpecies = instanceVariables.getSpecies().get(0);
        instanceSpecies.setMutationEnabled(false);
        Creature instanceCreature = new Creature(instanceSpecies, instanceVariables, c);

        World w = new World(_worldSize, instanceVariables);
        for (int i = 0; i < _initialPopulation; i++)
        {
            w.addImmigrant(new Creature(instanceCreature));
        }

        // Run simulation
        double tenLifespans = c.getSpecies().getMaximumAge() * 10;
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
