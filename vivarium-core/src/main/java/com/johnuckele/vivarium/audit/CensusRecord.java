package com.johnuckele.vivarium.audit;

import java.util.ArrayList;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class CensusRecord extends AuditRecord
{
    @SerializedParameter
    private AuditFunction _auditFunction;

    @SerializedParameter
    private ArrayList<Integer> _recordTicks;
    @SerializedParameter
    private ArrayList<Integer> _creaturePopulation;

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

    public static CensusRecord makeUninitialized()
    {
        return new CensusRecord();
    }

    public static CensusRecord makeWithSpecies(CensusFunction function, Species species)
    {
        return new CensusRecord(function, species);
    }
}
