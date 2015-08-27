package com.johnuckele.vivarium.audit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class Census extends AuditRecord
{
    protected Species          _trackedSpecies;
    private AuditFunction      _auditFunction;
    private ArrayList<Integer> _creaturePopulation;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("creaturePopulation", Map.class));
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("auditFunction", Map.class, SerializationCategory.AUDIT_FUNCTION));
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("trackedSpecies", Species.class, SerializationCategory.SPECIES));
    }

    private Census()
    {
    }

    public Census(Species species)
    {
        _trackedSpecies = species;
        _creaturePopulation = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getPopulationRecords()
    {
        return _creaturePopulation;
    }

    @Override
    public void record(World world, int tick)
    {
        _creaturePopulation.add(world.getCount(_trackedSpecies));
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
            case "creaturePopulation":
                return _creaturePopulation;
            case "trackedSpecies":
                return _trackedSpecies;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "creaturePopulation":
                _creaturePopulation = (ArrayList<Integer>) value;
                break;
            case "trackedSpecies":
                _trackedSpecies = (Species) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    public static Census makeUninitialized()
    {
        return new Census();
    }

    public static Census makeWithSpecies(Species species)
    {
        return new Census(species);
    }
}
