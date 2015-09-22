package com.johnuckele.vivarium.serialization;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.johnuckele.vivarium.audit.AuditFunction;
import com.johnuckele.vivarium.audit.AuditRecord;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.brain.Brain;

public enum SerializationCategory
{
    AUDIT_FUNCTION, AUDIT_RECORD, BRAIN, BLUEPRINT, CREATURE, SPECIES, WORLD;

    public static List<SerializationCategory> rankedValues()
    {
        return Collections.unmodifiableList(Arrays.asList(RANKED_VALUES));
    }

    public static SerializationCategory getCategoryForClass(Class<?> clazz)
    {
        if (AuditFunction.class.isAssignableFrom(clazz))
        {
            return AUDIT_FUNCTION;
        }
        else if (AuditRecord.class.isAssignableFrom(clazz))
        {
            return AUDIT_RECORD;
        }
        else if (Brain.class.isAssignableFrom(clazz))
        {
            return BRAIN;
        }
        else if (Blueprint.class.isAssignableFrom(clazz))
        {
            return BLUEPRINT;
        }
        else if (Creature.class.isAssignableFrom(clazz))
        {
            return CREATURE;
        }
        else if (Species.class.isAssignableFrom(clazz))
        {
            return SPECIES;
        }
        else if (World.class.isAssignableFrom(clazz))
        {
            return WORLD;
        }
        else
        {
            throw new IllegalStateException("Unable to determine serialization category of " + clazz);
        }
    }

    private static final SerializationCategory[] RANKED_VALUES = { SPECIES, AUDIT_FUNCTION, BLUEPRINT, BRAIN, CREATURE,
            AUDIT_RECORD, WORLD };
}