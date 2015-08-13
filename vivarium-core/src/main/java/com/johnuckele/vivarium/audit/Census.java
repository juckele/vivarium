package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class Census extends AuditRecord
{
    private Map<Species, int[]> _creaturePopulation;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("outputs", double[].class));
    }

    @Override
    public void record(World world, int tick)
    {
        // TODO : check performance of this
        for (Species species : _creaturePopulation.keySet())
        {
            world.getCount(species);
        }
        // TODO Auto-generated method stub

    }

    @Override
    public List<MapSerializer> getReferences()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "x":
                return null;
            case "y":
                return null;
            case "z":
                return null;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "x":
                break;
            case "y":
                break;
            case "z":
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }
}
