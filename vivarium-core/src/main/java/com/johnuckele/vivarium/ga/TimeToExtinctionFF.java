package com.johnuckele.vivarium.ga;

import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;

public class TimeToExtinctionFF extends SimulationBasedFitnessFunction
{
    private int       _initialPopulation;
    private Blueprint _blueprint;
    private double    _simulationDuration;

    public TimeToExtinctionFF(Blueprint blueprint, int initialPopulation, int simulationDuration)
    {
        this._blueprint = blueprint;
        this._initialPopulation = initialPopulation;
        this._simulationDuration = simulationDuration;
    }

    @Override
    public double evaluate(Creature c)
    {
        // Build world
        Blueprint instanceBlueprint = Blueprint.makeCopy(_blueprint);
        assert(instanceBlueprint.getSpecies().size() == 1);
        Species instanceSpecies = instanceBlueprint.getSpecies().get(0);
        instanceSpecies.setMutationRateExponent(Double.NEGATIVE_INFINITY);
        Creature instanceCreature = new Creature(instanceSpecies, c);

        World w = new World(instanceBlueprint);
        for (int i = 0; i < _initialPopulation; i++)
        {
            w.addImmigrant(new Creature(instanceCreature));
        }

        // Run simulation
        for (int i = 0; i < _simulationDuration; i++)
        {
            int count = w.getCount(EntityType.CREATURE);
            if (count == 0)
            {
                return i / _simulationDuration;
            }
            w.tick();
        }
        return 1.0;
    }
}
