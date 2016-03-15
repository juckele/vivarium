package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Rand;
import io.vivarium.visualization.RenderCode;
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
        this._outputs = new double[processorBlueprint.getTotalProcessorOutputCount()];
    }

    public RandomGenerator(int totalProcessorOutputCount)
    {
        this._outputs = new double[totalProcessorOutputCount];
    }

    private RandomGenerator()
    {
    }

    @Override
    public void normalizeWeights(double normalizedLength)
    {
        // No weights, nothing to normalize
    }

    @Override
    public double getGenomeLength()
    {
        // No values, no length
        return 0;
    }

    @Override
    public ProcessorType getProcessorType()
    {
        return ProcessorType.RANDOM;
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
    public String render(RenderCode code)
    {
        return "Hand coded processor: no render available";
    }

    public static Processor makeUninitialized()
    {
        return new RandomGenerator();
    }

    public static RandomGenerator makeWithCreatureBlueprint(ProcessorBlueprint processorBlueprint)
    {
        RandomGenerator processor = new RandomGenerator(processorBlueprint.getTotalProcessorOutputCount());
        return processor;
    }

    public static Processor makeWithParents(ProcessorBlueprint processorBlueprint,
            RandomGenerator untypedParentProcessor1, RandomGenerator untypedParentProcessor2)
    {
        RandomGenerator parentProcessor1 = untypedParentProcessor1;
        RandomGenerator parentProcessor2 = untypedParentProcessor2;
        RandomGenerator processor = new RandomGenerator(processorBlueprint, parentProcessor1, parentProcessor2);
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
