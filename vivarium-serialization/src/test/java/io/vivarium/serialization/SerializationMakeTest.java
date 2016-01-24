/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.serialization;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.processor.Processor;
import io.vivarium.core.processor.ProcessorType;

public class SerializationMakeTest
{
    @Test
    public void testWorldBlueprintMakeDefault() throws Exception
    {
        Blueprint blueprint = Blueprint.makeDefault();
        Tester.isNotNull("Blueprint should exist", blueprint);
    }

    @Test
    public void testWorldBlueprintMakeCopy() throws Exception
    {
        Blueprint blueprint = Blueprint.makeDefault();
        Blueprint copy = new SerializationEngine().makeCopy(blueprint);
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
        Species copy = new SerializationEngine().makeCopy(species);
        Tester.isNotNull("Species copy should exist", copy);
    }

    @Test
    public void testProcessorMakeWithSpecies() throws Exception
    {
        for (ProcessorType processorType : ProcessorType.values())
        {
            Species species = Species.makeDefault();
            species.setProcessorType(processorType);
            Processor processor = ProcessorType.makeWithSpecies(processorType, species);
            Tester.isNotNull("Processor of type " + processorType + " should exist", processor);
        }
    }

    @Test
    public void testProcessorMakeCopy() throws Exception
    {
        for (ProcessorType processorType : ProcessorType.values())
        {
            Species species = Species.makeDefault();
            species.setProcessorType(processorType);
            Processor processor = ProcessorType.makeWithSpecies(processorType, species);
            Processor copy = new SerializationEngine().makeCopy(processor);
            Tester.isNotNull("Processor copy of type " + processorType + "should exist", copy);
        }
    }

    @Test
    public void testCreatureMakeCopy() throws Exception
    {
        Species species = Species.makeDefault();
        Creature creature = new Creature(species);
        Creature copy = new SerializationEngine().makeCopy(creature);
        Tester.isNotNull("Creature copy should exist", copy);
    }

    @Test
    public void testWorldMakeCopy() throws Exception
    {
        Blueprint blueprint = Blueprint.makeDefault();
        World world = new World(blueprint);
        World copy = new SerializationEngine().makeCopy(world);
        Tester.isNotNull("World copy should exist", copy);
    }
}
