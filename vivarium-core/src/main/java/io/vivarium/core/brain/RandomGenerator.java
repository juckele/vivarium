/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core.brain;

import io.vivarium.core.Species;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Rand;
import io.vivarium.visualization.RenderCode;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class RandomGenerator extends Processor
{
    @SerializedParameter
    private double[] _outputs;

    private RandomGenerator(Species species, RandomGenerator parentBrain1, RandomGenerator parentBrain2)
    {
        // Random brain has no state, it's literally random output. This brain
        // does not evolve.
        this._outputs = new double[species.getTotalBrainOutputCount()];
    }

    public RandomGenerator(int totalBrainOutputCount)
    {
        this._outputs = new double[totalBrainOutputCount];
    }

    private RandomGenerator()
    {
    }

    @Override
    public void normalizeWeights()
    {
        // No weights, nothing to normalize
    }

    @Override
    public ProcessorType getBrainType()
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
        return "Hand coded brain: no render available";
    }

    public static Processor makeUninitialized()
    {
        return new RandomGenerator();
    }

    public static RandomGenerator makeWithSpecies(Species species)
    {
        RandomGenerator brain = new RandomGenerator(species.getTotalBrainOutputCount());
        return brain;
    }

    public static Processor makeWithParents(Species species, RandomGenerator untypedParentBrain1,
            RandomGenerator untypedParentBrain2)
    {
        RandomGenerator parentBrain1 = untypedParentBrain1;
        RandomGenerator parentBrain2 = untypedParentBrain2;
        RandomGenerator brain = new RandomGenerator(species, parentBrain1, parentBrain2);
        return brain;
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
