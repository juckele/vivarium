/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core.brain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.vivarium.core.Species;
import io.vivarium.visualization.RenderCode;

public enum ProcessorType
{
    NEURAL_NETWORK, RANDOM;

    public static String render(ProcessorType type, Collection<Processor> untypedBrains)
    {
        StringBuilder brainOutput = new StringBuilder();
        switch (type)
        {
            case NEURAL_NETWORK:
                List<NeuralNetwork> brains = new LinkedList<NeuralNetwork>();
                for (Processor untypedBrain : untypedBrains)
                {
                    brains.add((NeuralNetwork) untypedBrain);
                }
                NeuralNetwork medianBrain = NeuralNetwork.medianBrain(brains);
                NeuralNetwork standardDeviationBrain = NeuralNetwork.standardDeviationBrain(brains,
                        medianBrain);
                brainOutput.append("Average creature NN:\n");
                brainOutput.append(medianBrain.render(RenderCode.BRAIN_WEIGHTS));
                brainOutput.append("Std. Deviation on creature NNs:\n");
                brainOutput.append(standardDeviationBrain.render(RenderCode.BRAIN_WEIGHTS));
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

    public static Processor makeUninitialized(ProcessorType type)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetwork.makeUninitialized();
            case RANDOM:
                return RandomGenerator.makeUninitialized();
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }

    public static Processor makeWithSpecies(ProcessorType type, Species species)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetwork.makeWithSpecies(species);
            case RANDOM:
                return RandomGenerator.makeWithSpecies(species);
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }

    public static Processor makeWithParents(ProcessorType type, Species species, Processor untypedParentBrain1,
            Processor untypedParentBrain2)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetwork.makeWithParents(species, (NeuralNetwork) untypedParentBrain1,
                        (NeuralNetwork) untypedParentBrain2);
            case RANDOM:
                return RandomGenerator.makeWithParents(species, (RandomGenerator) untypedParentBrain1,
                        (RandomGenerator) untypedParentBrain2);
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }

    public static Class<?> getBrainClass(ProcessorType type)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetwork.class;
            case RANDOM:
                return RandomGenerator.class;
            default:
                throw new IllegalArgumentException("BrainType " + type + " not fully implemented");
        }
    }
}
