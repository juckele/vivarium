package io.vivarium.ga;

import io.vivarium.core.Creature;
import io.vivarium.core.brain.NeuralNetworkBrain;

public class GenomeWeightFF extends FitnessFunction
{

    @Override
    public double evaluate(Creature c)
    {
        NeuralNetworkBrain b = (NeuralNetworkBrain) c.getBrain();
        return b.getWeights()[0][0][0];
    }

}
