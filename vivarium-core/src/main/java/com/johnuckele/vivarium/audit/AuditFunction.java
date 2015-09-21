package com.johnuckele.vivarium.audit;

import java.util.Map;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
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

    public static AuditFunction makeFromMap(Map<String, Object> auditFunctionValues)
    {
        if (!auditFunctionValues.containsKey("auditType"))
        {
            throw new IllegalStateException("Unable to determine audit function type from map options.");
        }
        AuditType auditType = AuditType.valueOf((String) auditFunctionValues.get("auditType"));
        return auditType.makeFunctionFromMap(auditFunctionValues);
    }

    public static AuditFunction makeCopy(AuditRecord original)
    {
        return (AuditFunction) new SerializationEngine().makeCopy(original);
    }

    @Override
    public void finalizeSerialization()
    {
        // TODO Auto-generated method stub

    }

}
