package io.vivarium.serialization;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.core.processor.Processor;
import io.vivarium.core.processor.ProcessorBlueprint;
import io.vivarium.core.processor.ProcessorType;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class SerializationMakeTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldBlueprintMakeDefault() throws Exception
    {
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        Tester.isNotNull("Blueprint should exist", blueprint);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldBlueprintMakeCopy() throws Exception
    {
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        WorldBlueprint copy = new SerializationEngine().makeCopy(blueprint);
        Tester.isNotNull("Blueprint copy should exist", copy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testSpeciesMakeDefault() throws Exception
    {
        CreatureBlueprint species = CreatureBlueprint.makeDefault();
        Tester.isNotNull("Species should exist", species);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testSpeciesMakeCopy() throws Exception
    {
        CreatureBlueprint species = CreatureBlueprint.makeDefault();
        CreatureBlueprint copy = new SerializationEngine().makeCopy(species);
        Tester.isNotNull("Species copy should exist", copy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testProcessorMakeWithSpecies() throws Exception
    {
        for (ProcessorType processorType : ProcessorType.values())
        {
            ProcessorBlueprint processorArchitecture = ProcessorBlueprint.makeDefault();
            processorArchitecture.setProcessorType(processorType);
            CreatureBlueprint species = CreatureBlueprint.makeDefault();
            species.setProcessorBlueprint(processorArchitecture);
            Processor processor = ProcessorType.makeWithSpecies(processorType, species);
            Tester.isNotNull("Processor of type " + processorType + " should exist", processor);
        }
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testProcessorMakeCopy() throws Exception
    {
        for (ProcessorType processorType : ProcessorType.values())
        {
            ProcessorBlueprint processorBlueprint = ProcessorBlueprint.makeDefault();
            processorBlueprint.setProcessorType(processorType);
            CreatureBlueprint species = CreatureBlueprint.makeDefault();
            species.setProcessorBlueprint(processorBlueprint);
            Processor processor = ProcessorType.makeWithSpecies(processorType, species);
            Processor copy = new SerializationEngine().makeCopy(processor);
            Tester.isNotNull("Processor copy of type " + processorType + "should exist", copy);
        }
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testCreatureMakeCopy() throws Exception
    {
        CreatureBlueprint species = CreatureBlueprint.makeDefault();
        Creature creature = new Creature(species);
        Creature copy = new SerializationEngine().makeCopy(creature);
        Tester.isNotNull("Creature copy should exist", copy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldMakeCopy() throws Exception
    {
        WorldBlueprint blueprint = WorldBlueprint.makeDefault();
        World world = new World(blueprint);
        World copy = new SerializationEngine().makeCopy(world);
        Tester.isNotNull("World copy should exist", copy);
    }
}
