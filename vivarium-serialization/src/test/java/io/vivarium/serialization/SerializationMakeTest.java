package io.vivarium.serialization;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
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
        WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
        Tester.isNotNull("WorldBlueprint should exist", worldBlueprint);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldBlueprintMakeCopy() throws Exception
    {
        WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
        WorldBlueprint copy = new SerializationEngine().makeCopy(worldBlueprint);
        Tester.isNotNull("WorldBlueprint copy should exist", copy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testCreatureBlueprintMakeDefault() throws Exception
    {
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        Tester.isNotNull("CreatureBlueprint should exist", creatureBlueprint);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testCreatureBlueprintMakeCopy() throws Exception
    {
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        CreatureBlueprint copy = new SerializationEngine().makeCopy(creatureBlueprint);
        Tester.isNotNull("CreatureBlueprint copy should exist", copy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testProcessorMakeWithCreatureBlueprint() throws Exception
    {
        for (ProcessorType processorType : ProcessorType.values())
        {
            ProcessorBlueprint processorArchitecture = ProcessorBlueprint.makeDefault();
            processorArchitecture.setProcessorType(processorType);
            CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
            creatureBlueprint.setProcessorBlueprint(processorArchitecture);
            Processor processor = ProcessorType.makeDefaultProcessorBlueprint(processorType).makeProcessor(7, 3);
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
            CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
            creatureBlueprint.setProcessorBlueprint(processorBlueprint);
            Processor processor = ProcessorType.makeDefaultProcessorBlueprint(processorType).makeProcessor(2, 9);
            Processor copy = new SerializationEngine().makeCopy(processor);
            Tester.isNotNull("Processor copy of type " + processorType + "should exist", copy);
        }
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testCreatureMakeCopy() throws Exception
    {
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        Creature creature = new Creature(creatureBlueprint);
        Creature copy = new SerializationEngine().makeCopy(creature);
        Tester.isNotNull("Creature copy should exist", copy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldMakeCopy() throws Exception
    {
        WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
        World world = new World(worldBlueprint);
        World copy = new SerializationEngine().makeCopy(world);
        Tester.isNotNull("World copy should exist", copy);
    }
}
