package io.vivarium.audit;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.serialization.ClassRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CensusBlueprint extends AuditBlueprint
{
    static
    {
        ClassRegistry.getInstance().register(CensusBlueprint.class);
    }

    public CensusBlueprint()
    {
        super(AuditType.CENSUS);
    }

    @Override
    public CensusRecord makeRecordWithCreatureBlueprint(CreatureBlueprint creatureBlueprint)
    {
        return new CensusRecord(this, creatureBlueprint);
    }
}
