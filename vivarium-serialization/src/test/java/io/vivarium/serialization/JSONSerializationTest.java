package io.vivarium.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.Lists;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.GridWorld;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class JSONSerializationTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldSerializeAndDeserialize()
    {
        // Build a world with creatures
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
        worldBlueprint.setCreatureBlueprints(Lists.newArrayList(creatureBlueprint));
        GridWorld world = new GridWorld(worldBlueprint);

        // Convert to json
        String jsonString = JSONConverter.serializerToJSONString(world);

        // Deserialize
        GridWorld deserializeWorld = JSONConverter.jsonStringToSerializerCollection(jsonString).getFirst(GridWorld.class);

        // Deep compare of the worlds
        assertEquals(world, deserializeWorld);
    }
}
