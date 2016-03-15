package io.vivarium.audit;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class AuditBlueprint extends VivariumObject
{
    @SerializedParameter
    protected AuditType _auditType;

    protected AuditBlueprint(AuditType auditType)
    {
        this._auditType = auditType;
    }

    public AuditType getAuditType()
    {
        return _auditType;
    }

    public abstract AuditRecord makeRecordWithCreatureBlueprint(CreatureBlueprint blueprint);

    @Override
    public void finalizeSerialization()
    {
    }
}
