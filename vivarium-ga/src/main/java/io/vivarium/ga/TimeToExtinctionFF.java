package io.vivarium.ga;

import com.google.common.base.Preconditions;

import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.serialization.SerializationEngine;

public class TimeToExtinctionFF extends SimulationBasedFitnessFunction
{
    private int _initialPopulation;
    private WorldBlueprint _worldBlueprint;
    private double _simulationDuration;

    public TimeToExtinctionFF(WorldBlueprint worldBlueprint, int initialPopulation, int simulationDuration)
    {
        this._worldBlueprint = worldBlueprint;
        this._initialPopulation = initialPopulation;
        this._simulationDuration = simulationDuration;
    }

    @Override
    public double evaluate(Creature c)
    {
        // Build world
        WorldBlueprint instanceBlueprint = new SerializationEngine().makeCopy(_worldBlueprint);
        Preconditions.checkArgument(instanceBlueprint.getCreatureBlueprints().size() == 1);
        CreatureBlueprint instanceCreatureBlueprint = instanceBlueprint.getCreatureBlueprints().get(0);
        instanceCreatureBlueprint.getProcessorBlueprint().setMutationRateExponent(Double.NEGATIVE_INFINITY);
        Creature instanceCreature = new Creature(instanceCreatureBlueprint, c);

        World w = new World(instanceBlueprint);
        for (int i = 0; i < _initialPopulation; i++)
        {
            w.addImmigrant(new Creature(instanceCreature));
        }

        // Run simulation
        for (int i = 0; i < _simulationDuration; i++)
        {
            int count = w.getCreatureCount();
            if (count == 0)
            {
                return i / _simulationDuration;
            }
            w.tick();
        }
        return 1.0;
    }
}
