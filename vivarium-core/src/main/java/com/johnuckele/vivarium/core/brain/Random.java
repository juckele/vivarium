package com.johnuckele.vivarium.core.brain;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class Random extends Brain
{
    private double[] _outputs;

    public Random(Species species, Random parentBrain1, Random parentBrain2)
    {
        // Random brain has no state, it's literally random output. This brain
        // does not evolve.
        this._outputs = new double[species.getTotalBrainOutputCount()];
    }

    public Random(int totalBrainOutputCount)
    {
        this._outputs = new double[totalBrainOutputCount];
    }

    @Override
    public BrainType getBrainType()
    {
        return BrainType.RANDOM;
    }

    @Override
    public double[] outputs(double[] inputs)
    {
        for (int i = 0; i < _outputs.length; i++)
        {
            _outputs[i] = Rand.getRandomDouble();
        }
        return _outputs;
    }

    @Override
    public String render(RenderCode code)
    {
        return "Hand coded brain: no render available";
    }

}
