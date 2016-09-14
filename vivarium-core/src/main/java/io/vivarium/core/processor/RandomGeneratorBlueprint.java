package io.vivarium.core.processor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class RandomGeneratorBlueprint extends ProcessorBlueprint
{
    private RandomGeneratorBlueprint()
    {
        super(0, 0);
    }

    private RandomGeneratorBlueprint(int inputCount, int outputCount)
    {
        super(inputCount, outputCount);
    }

    @Override
    public void finalizeSerialization()
    {
    }

    @Override
    public RandomGenerator makeProcessor()
    {
        return RandomGenerator.makeWithProcessorBlueprint(this, getInputCount(), getOutputCount());
    }

    @Override
    public RandomGenerator makeProcessorWithParents(Processor parent1, Processor parent2)
    {
        return RandomGenerator.makeWithParents(this, parent1, parent2);
    }

    public static RandomGeneratorBlueprint makeDefault(int inputCount, int outputCount)
    {
        RandomGeneratorBlueprint a = new RandomGeneratorBlueprint(inputCount, outputCount);
        a.finalizeSerialization();
        return a;
    }
}
