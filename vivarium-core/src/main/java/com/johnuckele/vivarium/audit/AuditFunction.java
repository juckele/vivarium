package com.johnuckele.vivarium.audit;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializedParameter;

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
