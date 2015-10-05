package io.vivarium.serialization;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.vivarium.audit.AuditFunction;
import io.vivarium.audit.AuditRecord;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.brain.Brain;
import io.vivarium.core.simulation.Hook;
import io.vivarium.core.simulation.Simulation;

public enum SerializationCategory
{
    AUDIT_FUNCTION, AUDIT_RECORD, BRAIN, BLUEPRINT, CREATURE, SPECIES, WORLD, HOOK, SIMULATION;

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
        else if (Hook.class.isAssignableFrom(clazz))
        {
            return HOOK;
        }
        else if (Simulation.class.isAssignableFrom(clazz))
        {
            return SIMULATION;
        }
        else
        {
            throw new IllegalStateException("Unable to determine serialization category of " + clazz);
        }
    }

    private static final SerializationCategory[] RANKED_VALUES = { SPECIES, AUDIT_FUNCTION, BLUEPRINT, BRAIN, CREATURE,
            AUDIT_RECORD, WORLD, HOOK, SIMULATION };
}