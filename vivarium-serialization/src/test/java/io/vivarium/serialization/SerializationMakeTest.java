package io.vivarium.serialization;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.GridWorld;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.processor.DecisionTree;
import io.vivarium.core.processor.DecisionTreeBlueprint;
import io.vivarium.core.processor.NeuralNetwork;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.core.processor.RandomGenerator;
import io.vivarium.core.processor.RandomGeneratorBlueprint;
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
    public void testNeuralNetworkMakeDefault() throws Exception
    {
        NeuralNetworkBlueprint processorBlueprint = NeuralNetworkBlueprint.makeDefault(7, 3);
        Tester.isNotNull("NeuralNetworkBlueprint should exist", processorBlueprint);
        NeuralNetwork processor = processorBlueprint.makeProcessor();
        Tester.isNotNull("NeuralNetwork should exist", processor);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testNeuralNetworkMakeCopy() throws Exception
    {
        NeuralNetworkBlueprint processorBlueprint = NeuralNetworkBlueprint.makeDefault(7, 3);
        NeuralNetworkBlueprint blueprintCopy = new SerializationEngine().makeCopy(processorBlueprint);
        Tester.isNotNull("NeuralNetworkBlueprint copy should exist", blueprintCopy);
        NeuralNetwork processor = processorBlueprint.makeProcessor();
        NeuralNetwork processorCopy = new SerializationEngine().makeCopy(processor);
        Tester.isNotNull("NeuralNetwork copy should exist", processorCopy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testDecisionTreeMakeDefault() throws Exception
    {
        DecisionTreeBlueprint processorBlueprint = DecisionTreeBlueprint.makeDefault(7, 3);
        Tester.isNotNull("DecisionTreeBlueprint should exist", processorBlueprint);
        DecisionTree processor = processorBlueprint.makeProcessor();
        Tester.isNotNull("DecisionTree should exist", processor);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testDecisionTreeMakeCopy() throws Exception
    {
        DecisionTreeBlueprint processorBlueprint = DecisionTreeBlueprint.makeDefault(7, 3);
        DecisionTreeBlueprint blueprintCopy = new SerializationEngine().makeCopy(processorBlueprint);
        Tester.isNotNull("DecisionTreeBlueprint copy should exist", blueprintCopy);
        DecisionTree processor = processorBlueprint.makeProcessor();
        DecisionTree processorCopy = new SerializationEngine().makeCopy(processor);
        Tester.isNotNull("DecisionTree copy should exist", processorCopy);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testRandomGeneratorMakeDefault() throws Exception
    {
        RandomGeneratorBlueprint processorBlueprint = RandomGeneratorBlueprint.makeDefault(7, 3);
        Tester.isNotNull("RandomGeneratorBlueprint should exist", processorBlueprint);
        RandomGenerator processor = processorBlueprint.makeProcessor();
        Tester.isNotNull("RandomGenerator should exist", processor);
    }

    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testRandomGeneratorMakeCopy() throws Exception
    {
        RandomGeneratorBlueprint processorBlueprint = RandomGeneratorBlueprint.makeDefault(7, 3);
        RandomGeneratorBlueprint blueprintCopy = new SerializationEngine().makeCopy(processorBlueprint);
        Tester.isNotNull("RandomGeneratorBlueprint copy should exist", blueprintCopy);
        RandomGenerator processor = processorBlueprint.makeProcessor();
        RandomGenerator processorCopy = new SerializationEngine().makeCopy(processor);
        Tester.isNotNull("RandomGenerator copy should exist", processorCopy);
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
        GridWorld world = new GridWorld(worldBlueprint);
        GridWorld copy = new SerializationEngine().makeCopy(world);
        Tester.isNotNull("World copy should exist", copy);
    }
}
