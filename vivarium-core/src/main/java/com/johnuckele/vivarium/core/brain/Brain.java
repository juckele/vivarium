package com.johnuckele.vivarium.core.brain;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.visualization.RenderCode;

public abstract class Brain implements MapSerializer
{
    public abstract BrainType getBrainType();

    /**
     * Given a double of inputs to a brain, computes a set out outputs. This call returns a probabilistic solution, and
     * can vary between multiple calls, but the brain object stores no state that changes due to this method being
     * evoked.
     *
     * The input and output mapping is generated based on the species, and each species or creature object will have
     * code to map the arrays from world state and into actions.
     *
     * @param inputs
     * @return outputsS
     */
    public abstract double[] outputs(double[] inputs);

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.BRAIN;
    }

    public abstract String render(RenderCode code);

    public static Brain makeCopy(Brain original)
    {
        return (Brain) new SerializationEngine().makeCopy(original);
    }
}
