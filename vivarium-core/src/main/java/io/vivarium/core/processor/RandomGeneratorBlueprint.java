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
    }

    @Override
    public void finalizeSerialization()
    {
    }

    @Override
    public RandomGenerator makeProcessor(int inputs, int outputs)
    {
        return RandomGenerator.makeWithProcessorBlueprint(this, inputs, outputs);
    }

    @Override
    public RandomGenerator makeProcessorWithParents(Processor parent1, Processor parent2)
    {
        return RandomGenerator.makeWithParents(this, parent1, parent2);
    }

    public static RandomGeneratorBlueprint makeDefault()
    {
        RandomGeneratorBlueprint a = new RandomGeneratorBlueprint();
        a.finalizeSerialization();
        return a;
    }
}
