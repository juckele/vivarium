package io.vivarium.audit;

import io.vivarium.core.CreatureBlueprint;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class BreedingPatternBlueprint extends AuditBlueprint
{
    public BreedingPatternBlueprint()
    {
        super(AuditType.BREEDING_PATTERN);
    }

    @Override
    public BreedingPatternRecord makeRecordWithCreatureBlueprint(CreatureBlueprint creatureBlueprint)
    {
        return new BreedingPatternRecord(creatureBlueprint);
    }
}
