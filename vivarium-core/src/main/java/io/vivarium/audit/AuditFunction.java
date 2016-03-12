package io.vivarium.audit;

import io.vivarium.core.Species;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class AuditFunction extends VivariumObject
{
    @SerializedParameter
    protected AuditType _auditType;

    protected AuditFunction(AuditType auditType)
    {
        this._auditType = auditType;
    }

    public AuditType getAuditType()
    {
        return _auditType;
    }

    public abstract AuditRecord makeRecordWithSpecies(Species species);

    @Override
    public void finalizeSerialization()
    {
    }
}
