package com.johnuckele.vivarium.audit;

import java.util.Map;

import com.johnuckele.vivarium.serialization.SerializationEngine;

public class CensusFunction extends AuditFunction
{
    private CensusFunction()
    {
        super(AuditType.CENSUS);
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
