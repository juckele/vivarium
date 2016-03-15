package io.vivarium.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.Lists;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.processor.ProcessorBlueprint;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class JSONSerializationTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldSerializeAndDeserialize()
    {
        // Build a world with creatures
        ProcessorBlueprint processorBlueprint = ProcessorBlueprint.makeDefault();
        processorBlueprint.setRandomInitialization(true);

        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        creatureBlueprint.setProcessorBlueprint(processorBlueprint);
        creatureBlueprint.getProcessorBlueprint().setNormalizeAfterMutation(Math.sqrt(42));
        WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
        worldBlueprint.setCreatureBlueprints(Lists.newArrayList(creatureBlueprint));
        World world = new World(worldBlueprint);

        // Convert to json
        String jsonString = JSONConverter.serializerToJSONString(world);

        // Deserialize
        World deserializeWorld = JSONConverter.jsonStringToSerializerCollection(jsonString).getFirst(World.class);

        // Deep compare of the worlds
        assertEquals(world, deserializeWorld);
    }
}
