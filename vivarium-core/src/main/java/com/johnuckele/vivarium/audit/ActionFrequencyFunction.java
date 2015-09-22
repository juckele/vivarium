package com.johnuckele.vivarium.audit;

import java.util.Map;

import com.johnuckele.vivarium.serialization.SerializationEngine;

public class ActionFrequencyFunction extends AuditFunction
{
    private ActionFrequencyFunction()
    {
        super(AuditType.ACTION_FREQUENCY);
    }

    public static ActionFrequencyFunction makeUninitialized()
    {
        return new ActionFrequencyFunction();
    }

    public static ActionFrequencyFunction makeDefault()
    {
        return new ActionFrequencyFunction();
    }

    public static ActionFrequencyFunction makeFromMap(Map<String, Object> auditFunctionValues)
    {
        ActionFrequencyFunction af = new ActionFrequencyFunction();
        new SerializationEngine().deserialize(af, auditFunctionValues);
        return af;
    }
}
