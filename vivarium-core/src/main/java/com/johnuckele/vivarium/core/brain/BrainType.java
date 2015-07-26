package com.johnuckele.vivarium.core.brain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;

public enum BrainType
{
    NEURALNETWORK
    {
        @Override
        public String render(Collection<Brain> untypedBrains)
        {
            List<NeuralNetworkBrain> brains = new LinkedList<NeuralNetworkBrain>();
            for (Brain untypedBrain : untypedBrains)
            {
                brains.add((NeuralNetworkBrain) untypedBrain);
            }
            StringBuilder brainOutput = new StringBuilder();
            NeuralNetworkBrain medianBrain = NeuralNetworkBrain.medianBrain(brains);
            NeuralNetworkBrain standardDeviationBrain = NeuralNetworkBrain.standardDeviationBrain(brains, medianBrain);
            brainOutput.append("Average creature NN:\n");
            brainOutput.append(medianBrain.toString());
            brainOutput.append("Std. Deviation on creature NNs:\n");
            brainOutput.append(standardDeviationBrain.toString());
            /*
             * Brain minBrain = Brain.minBrain(brains); brainOutput.append("Min creature NN:\n");
             * brainOutput.append(minBrain.toString()); Brain maxBrain = Brain.maxBrain(brains);
             * brainOutput.append("Max creature NN:\n"); brainOutput.append(maxBrain.toString());
             */
            /*
             * brainOutput.append("Oldest creature NN:\n"); brainOutput.append(brains.get(0).toString());
             * brainOutput.append("Random creature NN:\n"); brainOutput.append(brains
             * .get(Rand.getRandomInt(brains.size())).toString());
             */
            return brainOutput.toString();
        }

        @Override
        public Brain ConstructBrain(Species species, Brain untypedParentBrain1, Brain untypedParentBrain2)
        {
            NeuralNetworkBrain parentBrain1 = (NeuralNetworkBrain) untypedParentBrain1;
            NeuralNetworkBrain parentBrain2 = (NeuralNetworkBrain) untypedParentBrain2;
            Brain brain = new NeuralNetworkBrain(species, parentBrain1, parentBrain2);
            return brain;
        }

        @Override
        public Brain ConstructBrain(Species species)
        {
            NeuralNetworkBrain brain = new NeuralNetworkBrain(species.getTotalBrainInputCount(),
                    species.getTotalBrainOutputCount(), species.getHiddenLayerCount(),
                    species.getRandomInitialization());
            return brain;
        }
    },
    RANDOM
    {
        @Override
        public String render(Collection<Brain> brains)
        {
            StringBuilder brainOutput = new StringBuilder();
            brainOutput.append("Hand coded brain: no render available");
            return brainOutput.toString();
        }

        @Override
        public Brain ConstructBrain(Species species, Brain untypedParentBrain1, Brain untypedParentBrain2)
        {
            Random parentBrain1 = (Random) untypedParentBrain1;
            Random parentBrain2 = (Random) untypedParentBrain2;
            Brain brain = new Random(species, parentBrain1, parentBrain2);
            return brain;
        }

        @Override
        public Brain ConstructBrain(Species species)
        {
            Random brain = new Random(species.getTotalBrainOutputCount());
            return brain;
        }
    };

    public abstract String render(Collection<Brain> brains);

    public abstract Brain ConstructBrain(Species species, Brain untypedParentBrain1, Brain untypedParentBrain2);

    public abstract Brain ConstructBrain(Species species);
}
