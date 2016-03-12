package io.vivarium.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.Lists;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.processor.ProcessorArchitecture;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class JSONSerializationTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testWorldSerializeAndDeserialize()
    {
        // Build a world with creatures
        ProcessorArchitecture processorArchitecture = ProcessorArchitecture.makeDefault();
        processorArchitecture.setRandomInitialization(true);

        Species species = Species.makeDefault();
        species.setProcessorArchitecture(processorArchitecture);
        species.setNormalizeAfterMutation(Math.sqrt(42));
        Blueprint blueprint = Blueprint.makeDefault();
        blueprint.setSpecies(Lists.newArrayList(species));
        World world = new World(blueprint);

        // Convert to json
        String jsonString = JSONConverter.serializerToJSONString(world);

        // Deserialize
        World deserializeWorld = JSONConverter.jsonStringToSerializerCollection(jsonString).getFirst(World.class);

        // Deep compare of the worlds
        assertEquals(world, deserializeWorld);
    }
}
