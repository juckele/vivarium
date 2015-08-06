package com.johnuckele.vivarium.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.WorldBlueprint;
import com.johnuckele.vtest.Tester;

public class SerializationCompletenessTest
{
    private Class[]  mapSerializers = { WorldBlueprint.class, Species.class };
    private String[] ignoredFields  = { "SERIALIZED_PARAMETERS", "_mutationRate" };

    @Test
    public void testCompleteness() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        for (Class mapSerializerClazz : mapSerializers)
        {
            // Find the fields on the class
            HashMap<String, Field> fields = new HashMap<String, Field>();
            for (Field field : mapSerializerClazz.getDeclaredFields())
            {
                fields.put(field.getName(), field);
            }
            // Create an instance of the MapSerializer class
            Method makeUnitializedMethod = mapSerializerClazz.getMethod("makeUninitialized");
            MapSerializer mapObject = (MapSerializer) makeUnitializedMethod.invoke(mapSerializerClazz);

            // Get the serializedParameters from the instance
            HashSet<SerializedParameter> serializedParameters = new HashSet<SerializedParameter>(
                    mapObject.getMappedParameters());
            for (String ignoredField : ignoredFields)
            {
                fields.remove(ignoredField);
            }

            // Find any fields that are not serializedParameters
            for (SerializedParameter parameter : serializedParameters)
            {
                String fieldName = parameter.getFieldName();
                Tester.contains("All mapped parameters must have a matching field", fields, fieldName);
                fields.remove(fieldName);
            }
            Tester.equal("No unmatched fields should remain:", fields.size(), 0);
        }
    }
}
