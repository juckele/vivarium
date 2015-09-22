package com.johnuckele.vivarium.core.brain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;

public enum BrainType
{
    NEURAL_NETWORK
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
             * brainOutput.append(minBrain.toString()); Brain maxBrain = Brain.maxBrain(brains); brainOutput.append(
             * "Max creature NN:\n"); brainOutput.append(maxBrain.toString());
             */
            /*
             * brainOutput.append("Oldest creature NN:\n"); brainOutput.append(brains.get(0).toString());
             * brainOutput.append("Random creature NN:\n"); brainOutput.append(brains
             * .get(Rand.getRandomInt(brains.size())).toString());
             */
            return brainOutput.toString();
        }

        @Override
        public Brain makeUninitialized()
        {
            return NeuralNetworkBrain.makeUninitialized();
        }

        @Override
        public Brain makeWithSpecies(Species species)
        {
            return NeuralNetworkBrain.makeWithSpecies(species);
        }

        @Override
        public Brain makeWithParents(Species species, Brain untypedParentBrain1, Brain untypedParentBrain2)
        {
            return NeuralNetworkBrain.makeWithParents(species, (NeuralNetworkBrain) untypedParentBrain1,
                    (NeuralNetworkBrain) untypedParentBrain2);
        }

        @Override
        public Class<?> getBrainClass()
        {
            return NeuralNetworkBrain.class;
        }
    },
    RANDOM
    {
        @Override
        public String render(Collection<Brain> brains)
        {
            StringBuilder brainOutput = new StringBuilder();
            brainOutput.append("Random action brain: no model render available");
            return brainOutput.toString();
        }

        @Override
        public Brain makeUninitialized()
        {
            return RandomBrain.makeUninitialized();
        }

        @Override
        public Brain makeWithSpecies(Species species)
        {
            return RandomBrain.makeWithSpecies(species);
        }

        @Override
        public Brain makeWithParents(Species species, Brain untypedParentBrain1, Brain untypedParentBrain2)
        {
            return RandomBrain.makeWithParents(species, (RandomBrain) untypedParentBrain1,
                    (RandomBrain) untypedParentBrain2);
        }

        @Override
        public Class<?> getBrainClass()
        {
            return RandomBrain.class;
        }
    };

    public abstract String render(Collection<Brain> brains);

    public abstract Brain makeUninitialized();

    public abstract Brain makeWithSpecies(Species species);

    public abstract Brain makeWithParents(Species species, Brain untypedParentBrain1, Brain untypedParentBrain2);

    public abstract Class<?> getBrainClass();
}
