package io.vivarium.audit;

import java.util.ArrayList;

import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.serialization.SerializedParameter;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
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
        super(species);
        _auditFunction = function;
        _creaturePopulation = new ArrayList<Integer>();
        _recordTicks = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getRecordTicks()
    {
        return _recordTicks;
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_auditFunction == null) ? 0 : _auditFunction.hashCode());
        result = prime * result + ((_creaturePopulation == null) ? 0 : _creaturePopulation.hashCode());
        result = prime * result + ((_recordTicks == null) ? 0 : _recordTicks.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        CensusRecord other = (CensusRecord) obj;
        if (_auditFunction == null)
        {
            if (other._auditFunction != null)
            {
                return false;
            }
        }
        else if (!_auditFunction.equals(other._auditFunction))
        {
            return false;
        }
        if (_creaturePopulation == null)
        {
            if (other._creaturePopulation != null)
            {
                return false;
            }
        }
        else if (!_creaturePopulation.equals(other._creaturePopulation))
        {
            return false;
        }
        if (_recordTicks == null)
        {
            if (other._recordTicks != null)
            {
                return false;
            }
        }
        else if (!_recordTicks.equals(other._recordTicks))
        {
            return false;
        }
        return true;
    }
}
