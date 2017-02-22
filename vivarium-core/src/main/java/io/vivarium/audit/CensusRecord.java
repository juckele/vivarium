package io.vivarium.audit;

import java.util.ArrayList;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CensusRecord extends AuditRecord
{
    static
    {
        ClassRegistry.getInstance().register(CensusRecord.class);
    }

    @SerializedParameter
    private AuditBlueprint _auditBlueprint;

    @SerializedParameter
    private ArrayList<Integer> _recordTicks;
    @SerializedParameter
    private ArrayList<Integer> _creaturePopulation;

    private CensusRecord()
    {
    }

    public CensusRecord(CensusBlueprint auditBlueprint, CreatureBlueprint creatureBlueprint)
    {
        super(creatureBlueprint);
        _auditBlueprint = auditBlueprint;
        _creaturePopulation = new ArrayList<>();
        _recordTicks = new ArrayList<>();
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
    public void record(GridWorld world, int tick)
    {
        // always record the starting population
        if (_creaturePopulation.size() < 1)
        {
            _recordTicks.add(tick);
            _creaturePopulation.add(world.getCount(_trackedCreatureBlueprint));
        }
        else
        {
            // Now get the current population and only record the new value if it has changed from the last record
            int currentCount = world.getCount(_trackedCreatureBlueprint);
            if (_creaturePopulation.get(_creaturePopulation.size() - 1) != currentCount)
            {
                _recordTicks.add(tick);
                _creaturePopulation.add(world.getCount(_trackedCreatureBlueprint));
            }
        }
    }

    public static CensusRecord makeUninitialized()
    {
        return new CensusRecord();
    }
}
