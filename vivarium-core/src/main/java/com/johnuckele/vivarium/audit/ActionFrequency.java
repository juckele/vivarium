package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class ActionFrequency extends AuditRecord
{
    protected Species _trackedSpecies;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("trackedSpecies", Species.class, SerializationCategory.SPECIES));
    }

    private ActionFrequency()
    {
    }

    protected ActionFrequency(Species species)
    {
    }

    @Override
    public void record(World world, int tick)
    {
        // TODO FILL
    }

    @Override
    public List<MapSerializer> getReferences()
    {
        LinkedList<MapSerializer> list = new LinkedList<MapSerializer>();
        list.add(_trackedSpecies);
        return list;
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
            case "trackedSpecies":
                return _trackedSpecies;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "trackedSpecies":
                _trackedSpecies = (Species) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    public static ActionFrequency makeUninitialized()
    {
        return new ActionFrequency();
    }

    public static ActionFrequency makeWithSpecies(Species species)
    {
        return new ActionFrequency(species);
    }
}
