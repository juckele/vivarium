package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class AuditFunction implements MapSerializer
{
    private AuditType _auditType;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("auditType", AuditType.class));
    }

    private AuditFunction()
    {
    }

    public AuditFunction(AuditType auditType)
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
    public List<SerializedParameter> getMappedParameters()
    {
        // TODO Auto-generated method stub
        return null;
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

    public static AuditFunction makeUninitialized()
    {
        return new AuditFunction();
    }

    public static AuditFunction makeCopy(AuditRecord original)
    {
        return (AuditFunction) new SerializationEngine().makeCopy(original);
    }
}
