package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;

public abstract class AuditFunction implements MapSerializer
{
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
    public List<MapSerializer> getReferences()
    {
        return new LinkedList<MapSerializer>();
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "auditType":
                return _auditType;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");

        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "auditType":
                _auditType = (AuditType) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.AUDIT_FUNCTION;
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
}
