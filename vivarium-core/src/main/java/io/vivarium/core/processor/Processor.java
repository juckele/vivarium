package io.vivarium.core.processor;

import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class Processor extends VivariumObject
{
    /**
     * Given a double of inputs to a processor, computes a set out outputs. This call returns a probabilistic solution,
     * and can vary between multiple calls, but the processor object stores no state that changes due to this method
     * being evoked.
     *
     * The input and output mapping is generated based on the creatures, the processor by design does not have
     * visibility to the mapping world state to inputs or from outputs to actions.
     *
     * @param inputs
     * @return outputsS
     */
    public abstract double[] outputs(double[] inputs);

    public abstract int getInputCount();

    public abstract int getOutputCount();

    @Override
    public void finalizeSerialization()
    {
    }
}
