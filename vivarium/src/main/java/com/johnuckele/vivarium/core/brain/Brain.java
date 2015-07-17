package com.johnuckele.vivarium.core.brain;

import java.io.Serializable;

public abstract class Brain implements Serializable
{
    private static final long serialVersionUID = 4913444344966201707L;

    public abstract BrainType getBrainType();

    /**
     * Given a double of inputs to a brain, computes a set out outputs. This
     * call returns a probabilistic solution, and can vary between multiple
     * calls, but the brain object stores no state that changes due to this
     * method being evoked.
     * 
     * The input and output mapping is generated based on the species, and each
     * species or creature object will have code to map the arrays from world
     * state and into actions.
     * 
     * @param inputs
     * @return outputs
     */
    public abstract double[] outputs(double[] inputs);
}
