package io.vivarium.ga;

import com.google.common.base.Preconditions;

import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.GridWorld;
import io.vivarium.core.GridWorldBlueprint;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.serialization.SerializationEngine;

public class TimeToExtinctionFF extends SimulationBasedFitnessFunction
{
    private int _initialPopulation;
    private GridWorldBlueprint _worldBlueprint;
    private double _simulationDuration;

    public TimeToExtinctionFF(GridWorldBlueprint worldBlueprint, int initialPopulation, int simulationDuration)
    {
        this._worldBlueprint = worldBlueprint;
        this._initialPopulation = initialPopulation;
        this._simulationDuration = simulationDuration;
    }

    @Override
    public double evaluate(Creature c)
    {
        // Build world
        GridWorldBlueprint instanceBlueprint = new SerializationEngine().makeCopy(_worldBlueprint);
        Preconditions.checkArgument(instanceBlueprint.getCreatureBlueprints().size() == 1);
        CreatureBlueprint instanceCreatureBlueprint = instanceBlueprint.getCreatureBlueprints().get(0);
        NeuralNetworkBlueprint processorBlueprint = (NeuralNetworkBlueprint) instanceCreatureBlueprint
                .getProcessorBlueprints()[0];
        processorBlueprint.setMutationRateExponent(Double.NEGATIVE_INFINITY);
        Creature instanceCreature = new Creature(instanceCreatureBlueprint, c);

        GridWorld w = new GridWorld(instanceBlueprint);
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
