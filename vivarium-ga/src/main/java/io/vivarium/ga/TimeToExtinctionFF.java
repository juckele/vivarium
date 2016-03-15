package io.vivarium.ga;

import com.google.common.base.Preconditions;

import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.EntityType;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.serialization.SerializationEngine;

public class TimeToExtinctionFF extends SimulationBasedFitnessFunction
{
    private int _initialPopulation;
    private WorldBlueprint _blueprint;
    private double _simulationDuration;

    public TimeToExtinctionFF(WorldBlueprint blueprint, int initialPopulation, int simulationDuration)
    {
        this._blueprint = blueprint;
        this._initialPopulation = initialPopulation;
        this._simulationDuration = simulationDuration;
    }

    @Override
    public double evaluate(Creature c)
    {
        // Build world
        WorldBlueprint instanceBlueprint = new SerializationEngine().makeCopy(_blueprint);
        Preconditions.checkArgument(instanceBlueprint.getCreatureBlueprints().size() == 1);
        CreatureBlueprint instanceCreatureBlueprint = instanceBlueprint.getCreatureBlueprints().get(0);
        instanceCreatureBlueprint.setMutationRateExponent(Double.NEGATIVE_INFINITY);
        Creature instanceCreature = new Creature(instanceCreatureBlueprint, c);

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
