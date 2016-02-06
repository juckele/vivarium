/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core.processor;

import io.vivarium.serialization.VivariumObject;
import io.vivarium.visualization.RenderCode;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class Processor extends VivariumObject
{
    public abstract ProcessorType getProcessorType();

    /**
     * Given a double of inputs to a processor, computes a set out outputs. This call returns a probabilistic solution,
     * and can vary between multiple calls, but the processor object stores no state that changes due to this method
     * being evoked.
     *
     * The input and output mapping is generated based on the species, and each species or creature object will have
     * code to map the arrays from world state and into actions.
     *
     * @param inputs
     * @return outputsS
     */
    public abstract double[] outputs(double[] inputs);

    public abstract int getInputCount();

    public abstract int getOutputCount();

    /**
     * Applies a processor defined normalization procedure to a processor.
     */
    public abstract void normalizeWeights(double normalizedLength);

    /**
     * Returns the length of the genome if measured as a high dimensional vector
     */
    public abstract double getGenomeLength();

    public abstract String render(RenderCode code);

    @Override
    public void finalizeSerialization()
    {
    }
}
