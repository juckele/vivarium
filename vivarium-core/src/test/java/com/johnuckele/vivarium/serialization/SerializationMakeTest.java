package com.johnuckele.vivarium.serialization;

import org.junit.Test;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.WorldBlueprint;
import com.johnuckele.vtest.Tester;

public class SerializationMakeTest
{
    @Test
    public void testWorldBlueprintMakeDefault() throws Exception
    {
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        Tester.isNotNull("Blueprint should exist", blueprint);
    }

    @Test
    public void testWorldBlueprintMakeCopy() throws Exception
    {
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        WorldBlueprint copy = WorldBlueprint.makeCopy(blueprint);
        Tester.isNotNull("Blueprint copy should exist", copy);
    }

    @Test
    public void testSpeciesMakeDefault() throws Exception
    {
        Species species = Species.makeDefault();
        Tester.isNotNull("Species should exist", species);
    }

    @Test
    public void testSpeciesMakeCopy() throws Exception
    {
        Species species = Species.makeDefault();
        Species copy = Species.makeCopy(species);
        Tester.isNotNull("Species copy should exist", copy);
    }
}
