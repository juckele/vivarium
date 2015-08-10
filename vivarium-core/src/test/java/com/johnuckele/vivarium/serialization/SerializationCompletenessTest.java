package com.johnuckele.vivarium.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.WorldBlueprint;
import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vtest.Tester;

public class SerializationCompletenessTest
{
    @Test
    public void testWorldBlueprintCompleteness() throws Exception
    {
        String[] ignoredFields = { };
        completenessTestHelper(WorldBlueprint.class, ignoredFields);
    }

    @Test
    public void testSpeciesCompleteness() throws Exception
    {
        String[] ignoredFields = { "_mutationRate" };
        completenessTestHelper(Species.class, ignoredFields);
    }

    @Test
    public void testBrainCompleteness() throws Exception
    {
        for (BrainType brainType : BrainType.values())
        {
            String[] ignoredFields = { };
            completenessTestHelper(brainType.getBrainClass(), ignoredFields);
        }
    }

    private void completenessTestHelper(Class<?> clazz, String[] ignoredFields) throws Exception
    {
        // Find the non-static fields on the class
        HashMap<String, Field> fields = new HashMap<String, Field>();
        for (Field field : clazz.getDeclaredFields())
        {
            if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
            {
                fields.put(field.getName(), field);
            }
        }
        // Create an instance of the MapSerializer class
        Method makeUnitializedMethod = clazz.getMethod("makeUninitialized");
        MapSerializer mapObject = (MapSerializer) makeUnitializedMethod.invoke(clazz);

        // Get the serializedParameters from the instance
        HashSet<SerializedParameter> serializedParameters = new HashSet<SerializedParameter>(
                mapObject.getMappedParameters());

        // Strip out explicitly ignored fields
        for (String ignoredField : ignoredFields)
        {
            Tester.contains("All ignored fields must have a matching field", fields, ignoredField);
            fields.remove(ignoredField);
        }

        // Find any fields that are not serializedParameters
        for (SerializedParameter parameter : serializedParameters)
        {
            String parameterFieldName = parameter.getFieldName();
            Tester.contains("All mapped parameters must have a matching field", fields, parameterFieldName);
            fields.remove(parameterFieldName);
        }
        Tester.equal("No unmatched fields should remain:", fields.size(), 0);
    }
}
