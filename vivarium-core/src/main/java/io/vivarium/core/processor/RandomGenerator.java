package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class RandomGenerator extends Processor
{
    @SerializedParameter
    private double[] _outputs;

    private RandomGenerator(ProcessorBlueprint processorBlueprint, RandomGenerator parentProcessor1,
            RandomGenerator parentProcessor2)
    {
        // Random processor has no state, it's literally random output. This processor
        // does not evolve.
        this._outputs = new double[_outputs.length];
    }

    public RandomGenerator(int inputs, int outputs)
    {
        this._outputs = new double[outputs];
    }

    private RandomGenerator()
    {
    }

    @Override
    public double[] outputs(double[] inputs)
    {
        for (int i = 0; i < _outputs.length; i++)
        {
            _outputs[i] = Rand.getInstance().getRandomDouble();
        }
        return _outputs;
    }

    @Override
    public double[] outputs()
    {
        return _outputs;
    }

    public static Processor makeUninitialized()
    {
        return new RandomGenerator();
    }

    public static RandomGenerator makeWithProcessorBlueprint(ProcessorBlueprint processorBlueprint, int inputs,
            int outputs)
    {
        RandomGenerator processor = new RandomGenerator(inputs, outputs);
        return processor;
    }

    public static RandomGenerator makeWithParents(ProcessorBlueprint processorBlueprint, Processor untypedProcessor1,
            Processor untypedProcessor2)
    {
        RandomGenerator parent1 = (RandomGenerator) untypedProcessor1;
        RandomGenerator parent2 = (RandomGenerator) untypedProcessor2;
        RandomGenerator processor = new RandomGenerator(processorBlueprint, parent1, parent2);
        return processor;
    }

    @Override
    public int getOutputCount()
    {
        return _outputs.length;
    }

    @Override
    public int getInputCount()
    {
        return 0;
    }
}
