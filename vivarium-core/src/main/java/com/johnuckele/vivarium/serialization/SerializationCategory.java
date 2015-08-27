package com.johnuckele.vivarium.serialization;

public enum SerializationCategory
{
    AUDIT_FUNCTION, AUDIT_RECORD, BRAIN, BLUEPRINT, CREATURE, SPECIES, WORLD;

    public static SerializationCategory[] rankedValues()
    {
        return result;
    }

    private static SerializationCategory[] result = { SPECIES, AUDIT_FUNCTION, BLUEPRINT, BRAIN, CREATURE, AUDIT_RECORD,
            WORLD };
}