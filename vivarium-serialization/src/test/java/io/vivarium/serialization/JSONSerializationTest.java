/*
 * Copyright © 2016 John H Uckele. All rights reserved.
 */

package io.vivarium.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.core.World;

public class JSONSerializationTest
{
    @Test
    public void testWorldSerializeAndDeserialize()
    {
        // Build a world with creatures
        Species species = Species.makeDefault();
        species.setNormalizeAfterMutation(Math.sqrt(42));
        species.setRandomInitialization(false);
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
