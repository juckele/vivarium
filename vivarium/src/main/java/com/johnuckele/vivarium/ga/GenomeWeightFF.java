package com.johnuckele.vivarium.ga;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;

public class GenomeWeightFF extends FitnessFunction
{

    @Override
    public double evaluate(Creature c)
    {
        NeuralNetworkBrain b = (NeuralNetworkBrain) c.getBrain();
        return b.getWeights()[0][0][0];
    }

}
