package com.johnuckele.vivarium.core.brain;

import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializedParameter;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class RandomBrain extends Brain
{
    private double[] _outputs;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("outputs", double[].class, "[]"));
    }

    private RandomBrain(Species species, RandomBrain parentBrain1, RandomBrain parentBrain2)
    {
        // Random brain has no state, it's literally random output. This brain
        // does not evolve.
        this._outputs = new double[species.getTotalBrainOutputCount()];
    }

    private RandomBrain(int totalBrainOutputCount)
    {
        this._outputs = new double[totalBrainOutputCount];
    }

    private RandomBrain()
    {
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

    @Override
    public List<MapSerializer> getReferences()
    {
        return new LinkedList<MapSerializer>();
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return RandomBrain.SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "outputs":
                return this._outputs;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "outputs":
                this._outputs = (double[]) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    public static Brain makeUninitialized()
    {
        return new RandomBrain();
    }

    public static RandomBrain makeWithSpecies(Species species)
    {
        RandomBrain brain = new RandomBrain(species.getTotalBrainOutputCount());
        return brain;
    }

    public static Brain makeWithParents(Species species, RandomBrain untypedParentBrain1,
            RandomBrain untypedParentBrain2)
    {
        RandomBrain parentBrain1 = untypedParentBrain1;
        RandomBrain parentBrain2 = untypedParentBrain2;
        RandomBrain brain = new RandomBrain(species, parentBrain1, parentBrain2);
        return brain;
    }

}
