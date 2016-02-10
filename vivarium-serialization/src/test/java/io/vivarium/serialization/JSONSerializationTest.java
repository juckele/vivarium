/*
 * Copyright © 2016 John H Uckele. All rights reserved.
 */

package io.vivarium.serialization;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.johnuckele.vtest.Tester;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.processor.NeuralNetwork;

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

        // Deep compare of creature brains
        LinkedList<Creature> originalCreatures = world.getCreatures();
        LinkedList<Creature> deserializedCreatures = deserializeWorld.getCreatures();
        Tester.equal("Creature counts should be the same", originalCreatures.size(), deserializedCreatures.size());
        for (int index = 0; index < originalCreatures.size(); index++)
        {
            Creature originalCreature = originalCreatures.get(index);
            Creature deserializedCreature = originalCreatures.get(index);
            if (originalCreature.getProcessor() instanceof NeuralNetwork)
            {
                NeuralNetwork originalCreatureProcessor = (NeuralNetwork) originalCreature.getProcessor();
                NeuralNetwork deserializedCreatureProcessor = (NeuralNetwork) deserializedCreature.getProcessor();
                double[][][] originalCreatureProcessorWeights = originalCreatureProcessor.getWeights();
                double[][][] deserializedCreatureProcessorWeights = deserializedCreatureProcessor.getWeights();
                compareNeuralNetworkWeights(originalCreatureProcessorWeights, deserializedCreatureProcessorWeights);
                assertEquals(Math.sqrt(42), originalCreatureProcessor.getGenomeLength(), 0.0000000001);
                assertEquals(Math.sqrt(42), deserializedCreatureProcessor.getGenomeLength(), 0.0000000001);
            }
        }
    }

    private void compareNeuralNetworkWeights(double[][][] weights1, double[][][] weights2)
    {
        for (int i = 0; i < weights1.length; i++)
        {
            for (int j = 0; j < weights1[i].length; j++)
            {
                for (int k = 0; k < weights1[i][j].length; k++)
                {
                    assertEquals(weights1[i][j][k], weights2[i][j][k], 0);
                }
            }
        }

    }
}
