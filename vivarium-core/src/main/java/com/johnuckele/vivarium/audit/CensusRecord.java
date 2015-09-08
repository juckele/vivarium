package com.johnuckele.vivarium.audit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class CensusRecord extends AuditRecord
{
    private AuditFunction _auditFunction;

    private ArrayList<Integer> _recordTicks;
    private ArrayList<Integer> _creaturePopulation;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("trackedSpecies", Species.class, SerializationCategory.SPECIES));
        SERIALIZED_PARAMETERS.add(
                new SerializedParameter("auditFunction", AuditFunction.class, SerializationCategory.AUDIT_FUNCTION));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("creaturePopulation", ArrayList.class, Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("recordTicks", ArrayList.class, Integer.class));
    }

    private CensusRecord()
    {
    }

    public CensusRecord(CensusFunction function, Species species)
    {
        _auditFunction = function;
        _trackedSpecies = species;
        _creaturePopulation = new ArrayList<Integer>();
        _recordTicks = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getPopulationRecords()
    {
        return _creaturePopulation;
    }

    @Override
    public void record(World world, int tick)
    {
        // always record the starting population
        if (_creaturePopulation.size() < 1)
        {
            _recordTicks.add(tick);
            _creaturePopulation.add(world.getCount(_trackedSpecies));
        }
        else
        {
            // Now get the current population and only record the new value if it has changed from the last record
            int currentCount = world.getCount(_trackedSpecies);
            if (_creaturePopulation.get(_creaturePopulation.size() - 1) != currentCount)
            {
                _recordTicks.add(tick);
                _creaturePopulation.add(world.getCount(_trackedSpecies));
            }
        }
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
            case "recordTicks":
                return _recordTicks;
            case "auditFunction":
                return _auditFunction;
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
            case "recordTicks":
                _recordTicks = (ArrayList<Integer>) value;
                break;
            case "auditFunction":
                _auditFunction = (AuditFunction) value;
                break;
            case "trackedSpecies":
                _trackedSpecies = (Species) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    public static CensusRecord makeUninitialized()
    {
        return new CensusRecord();
    }

    public static CensusRecord makeWithSpecies(CensusFunction function, Species species)
    {
        return new CensusRecord(function, species);
    }
}
