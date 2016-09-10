package io.vivarium.ga;

import io.vivarium.core.Creature;
import io.vivarium.core.processor.NeuralNetwork;
import io.vivarium.core.processor.Processor;

public class GenomeWeightFF extends FitnessFunction
{
    @Override
    public double evaluate(Creature c)
    {
        double weights = 0;
        Processor[] processors = c.getProcessors();
        for (Processor processor : processors)
        {
            if (NeuralNetwork.class.isAssignableFrom(processor.getClass()))
            {
                NeuralNetwork nn = (NeuralNetwork) processor;
                weights += nn.getWeights()[0][0];
            }
        }
        return weights;
    }

}
