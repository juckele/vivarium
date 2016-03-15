package io.vivarium.audit;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class AuditRecord extends VivariumObject
{
    @SerializedParameter
    protected CreatureBlueprint _trackedCreatureBlueprint;

    protected AuditRecord()
    {
    }

    protected AuditRecord(CreatureBlueprint blueprint)
    {
        _trackedCreatureBlueprint = blueprint;
    }

    /**
     * Inspect a world, recording any information required by the audit record
     *
     * @param inputs
     * @return outputsS
     */
    public abstract void record(World world, int tick);

    @Override
    public void finalizeSerialization()
    {
    }
}