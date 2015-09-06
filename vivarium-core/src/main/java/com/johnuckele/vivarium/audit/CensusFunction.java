package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class CensusFunction extends AuditFunction
{
    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("auditType", AuditType.class));
    }

    private CensusFunction()
    {
        super(AuditType.CENSUS);
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return SERIALIZED_PARAMETERS;
    }

    public static CensusFunction makeUninitialized()
    {
        return new CensusFunction();
    }

    public static CensusFunction makeDefault()
    {
        return new CensusFunction();
    }

    public static CensusFunction makeFromMap(Map<String, Object> auditFunctionValues)
    {
        CensusFunction af = new CensusFunction();
        new SerializationEngine().deserialize(af, auditFunctionValues);
        return af;
    }
}
