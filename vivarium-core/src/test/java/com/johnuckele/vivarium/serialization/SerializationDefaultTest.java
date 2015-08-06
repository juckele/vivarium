package com.johnuckele.vivarium.serialization;

import org.junit.Test;

import com.johnuckele.vivarium.core.WorldBlueprint;
import com.johnuckele.vtest.Tester;

public class SerializationDefaultTest
{
    @Test
    public void testWorldBlueprint() throws Exception
    {
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        Tester.isNotNull("Blueprint should exist", blueprint);
    }

}
