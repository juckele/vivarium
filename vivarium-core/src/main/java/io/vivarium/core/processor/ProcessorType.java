package io.vivarium.core.processor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.visualization.RenderCode;

public enum ProcessorType
{
    NEURAL_NETWORK, RANDOM;

    public static String render(ProcessorType type, Collection<Processor> untypedProcessors)
    {
        StringBuilder processorOutput = new StringBuilder();
        switch (type)
        {
            case NEURAL_NETWORK:
                List<NeuralNetwork> processors = new LinkedList<>();
                for (Processor untypedProcessor : untypedProcessors)
                {
                    processors.add((NeuralNetwork) untypedProcessor);
                }
                NeuralNetwork medianProcessor = NeuralNetwork.medianProcessor(processors);
                NeuralNetwork standardDeviationProcessor = NeuralNetwork.standardDeviationProcessor(processors,
                        medianProcessor);
                processorOutput.append("Average creature NN:\n");
                processorOutput.append(medianProcessor.render(RenderCode.PROCESSOR_WEIGHTS));
                processorOutput.append("Std. Deviation on creature NNs:\n");
                processorOutput.append(standardDeviationProcessor.render(RenderCode.PROCESSOR_WEIGHTS));
                /*
                 * Processor minProcessor = Processor.minProcessor(processors); processorOutput.append(
                 * "Min creature NN:\n"); processorOutput.append(minProcessor.toString()); Processor maxProcessor =
                 * Processor.maxProcessor(processors); processorOutput.append( "Max creature NN:\n");
                 * processorOutput.append(maxProcessor.toString());
                 */
                /*
                 * processorOutput.append("Oldest creature NN:\n");
                 * processorOutput.append(processors.get(0).toString()); processorOutput.append("Random creature NN:\n"
                 * ); processorOutput.append(processors .get(Rand.getRandomInt(processors.size())).toString());
                 */
                break;
            case RANDOM:
                processorOutput.append("Random action processor: no model render available");
                break;
            default:
                throw new IllegalArgumentException("ProcessorType " + type + " not fully implemented");
        }
        return processorOutput.toString();
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
                throw new IllegalArgumentException("ProcessorType " + type + " not fully implemented");
        }
    }

    public static Processor makeWithCreatureBlueprint(ProcessorType type, CreatureBlueprint creatureBlueprint)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetwork.makeWithCreatureBlueprint(creatureBlueprint);
            case RANDOM:
                return RandomGenerator.makeWithCreatureBlueprint(creatureBlueprint);
            default:
                throw new IllegalArgumentException("ProcessorType " + type + " not fully implemented");
        }
    }

    public static Processor makeWithParents(ProcessorType type, CreatureBlueprint creatureBlueprint,
            Processor untypedParentProcessor1, Processor untypedParentProcessor2)
    {
        switch (type)
        {
            case NEURAL_NETWORK:
                return NeuralNetwork.makeWithParents(creatureBlueprint, (NeuralNetwork) untypedParentProcessor1,
                        (NeuralNetwork) untypedParentProcessor2);
            case RANDOM:
                return RandomGenerator.makeWithParents(creatureBlueprint, (RandomGenerator) untypedParentProcessor1,
                        (RandomGenerator) untypedParentProcessor2);
            default:
                throw new IllegalArgumentException("ProcessorType " + type + " not fully implemented");
        }
    }
}
