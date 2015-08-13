package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class AuditRecord implements MapSerializer
{
    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        // TODO FILL
    }

    @Override
    public List<MapSerializer> getReferences()
    {
        return new LinkedList<MapSerializer>();
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return AuditRecord.SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "x":
                return null;
            case "y":
                return null;
            case "z":
                return null;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "x":
                break;
            case "y":
                break;
            case "z":
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.AUDIT;
    }

    public static AuditRecord makeUninitialized()
    {
        return new AuditRecord();
    }

    public static AuditRecord makeDefault()
    {
        AuditRecord audit = new AuditRecord();
        new SerializationEngine().deserialize(audit, SerializationEngine.EMPTY_OBJECT_MAP);
        return audit;
    }

    public static AuditRecord makeCopy(AuditRecord original)
    {
        return (AuditRecord) new SerializationEngine().makeCopy(original);
    }

}