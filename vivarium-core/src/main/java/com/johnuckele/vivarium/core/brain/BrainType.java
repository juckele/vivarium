package com.johnuckele.vivarium.core.brain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;

public enum BrainType
{
    NEURAL_NETWORK, RANDOM;

    public static String render(BrainType type, Collection<Brain> untypedBrains)
    {
        StringBuilder brainOutput = new StringBuilder();
        switch (type)
        {
            case NEURAL_NETWORK:
                List<NeuralNetworkBrain> brains = new LinkedList<NeuralNetworkBrain>();
                for (Brain untypedBrain : untypedBrains)
                {
                    brains.add((NeuralNetworkBrain) untypedBrain);
                }
                NeuralNetworkBrain medianBrain = NeuralNetworkBrain.medianBrain(brains);
                NeuralNetworkBrain standardDeviationBrain = NeuralNetworkBrain.standardDeviationBrain(brains,
                        medianBrain);
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
                break;
            case RANDOM:
                brainOutput.append("Random action brain: no model render available");
                break;
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
        return brainOutput.toString();
    }

    public static Brain makeUninitialized(BrainType type)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetworkBrain.makeUninitialized();
            case RANDOM:
                return RandomBrain.makeUninitialized();
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }

    public static Brain makeWithSpecies(BrainType type, Species species)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetworkBrain.makeWithSpecies(species);
            case RANDOM:
                return RandomBrain.makeWithSpecies(species);
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }

    public static Brain makeWithParents(BrainType type, Species species, Brain untypedParentBrain1,
            Brain untypedParentBrain2)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetworkBrain.makeWithParents(species, (NeuralNetworkBrain) untypedParentBrain1,
                        (NeuralNetworkBrain) untypedParentBrain2);
            case RANDOM:
                return RandomBrain.makeWithParents(species, (RandomBrain) untypedParentBrain1,
                        (RandomBrain) untypedParentBrain2);
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }

    public static Class<?> getBrainClass(BrainType type)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetworkBrain.class;
            case RANDOM:
                return RandomBrain.class;
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }
}
