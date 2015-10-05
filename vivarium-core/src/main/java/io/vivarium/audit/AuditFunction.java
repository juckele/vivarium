package io.vivarium.audit;

import io.vivarium.serialization.MapSerializer;
import io.vivarium.serialization.SerializedParameter;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class AuditFunction implements MapSerializer
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

    @Override
    public void finalizeSerialization()
    {
        // TODO Auto-generated method stub

    }

}
