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
    public RandomGenerator makeProcessor()
    {
        return RandomGenerator.makeWithProcessorBlueprint(this);
    }

    public static RandomGeneratorBlueprint makeDefault()
    {
        RandomGeneratorBlueprint a = new RandomGeneratorBlueprint();
        a.finalizeSerialization();
        return a;
    }
}
