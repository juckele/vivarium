package com.johnuckele.vivarium.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import com.johnuckele.vivarium.audit.AuditType;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vtest.Tester;

public class SerializationCompletenessTest
{
    @Test
    public void testWorldBlueprintCompleteness() throws Exception
    {
        String[] ignoredFields = {};
        completenessTestHelper(Blueprint.class, ignoredFields);
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
            String[] ignoredFields = {};
            completenessTestHelper(brainType.getBrainClass(), ignoredFields);
        }
    }

    @Test
    public void testAuditCompleteness() throws Exception
    {
        for (AuditType auditRecordType : AuditType.values())
        {
            String[] ignoredFields = {};
            completenessTestHelper(auditRecordType.getAuditRecordClass(), ignoredFields);
        }
    }

    @Test
    public void testCreatureCompleteness() throws Exception
    {
        String[] ignoredFields = {};
        completenessTestHelper(Creature.class, ignoredFields);
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
            Tester.contains("" + clazz + ": All ignored fields must have a matching field", fields, ignoredField);
            fields.remove(ignoredField);
        }

        // Find any fields that are not serializedParameters
        for (SerializedParameter parameter : serializedParameters)
        {
            String parameterFieldName = parameter.getFieldName();
            Tester.contains("" + clazz + ": All mapped parameters must have a matching field", fields,
                    parameterFieldName);
            fields.remove(parameterFieldName);
        }
        Tester.equal("" + clazz + ": No unmatched fields should remain:", fields.size(), 0);
    }
}
