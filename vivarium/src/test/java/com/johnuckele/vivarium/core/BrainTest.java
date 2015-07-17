package com.johnuckele.vivarium.core;

import org.junit.Test;

import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;
import com.johnuckele.vivarium.util.Functions;
import com.johnuckele.vtest.Tester;

public class BrainTest
{
	@Test public void testComputeLayerInPlaceConstantBias()
	{
		double[] inputs =
		{};
		double[] expectedOutputs =
		{ Functions.sigmoid(1) };
		double[] actualOutputs =
		{ 0.0 };
		double[][] weights =
		{
		{ 1, 0 } };
		NeuralNetworkBrain.computeLayerInPlace(inputs, actualOutputs, weights);
		Tester.equal("no inputs, output is = constant bias", actualOutputs[0], expectedOutputs[0], 0.0);
	}

	@Test public void testComputeLayerInPlaceWithoutBias()
	{
		double[] inputs =
		{ 1.0, 0.5, 0.0 };
		double[] expectedOutputs =
		{ Functions.sigmoid(1.5), Functions.sigmoid(2), Functions.sigmoid(-1), 0.5 };
		double[] actialOutputs = new double[4];
		double[][] weights =
		{
		{ 0, 0, 1, 1, 1 },
		{ 0, 0, 2, 0, 0 },
		{ 0, 0, -2, 2, 0 },
		{ 0, 0, 0, 0, 1 } };
		NeuralNetworkBrain.computeLayerInPlace(inputs, actialOutputs, weights);
		Tester.equal("3 inputs, 1st output", actialOutputs[0], expectedOutputs[0], 0.0);
		Tester.equal("3 inputs, 2nd output", actialOutputs[1], expectedOutputs[1], 0.0);
		Tester.equal("3 inputs, 3rd output", actialOutputs[2], expectedOutputs[2], 0.0);
		Tester.equal("3 inputs, 4th output", actialOutputs[3], expectedOutputs[3], 0.0);
	}

	@Test public void testComputeLayerInPlaceWithRandomBias()
	{
		double[] inputs =
		{};
		double[] maximumExpectedOutputs =
		{ Functions.sigmoid(1) };
		double[] minimumExpectedOutputs =
		{ Functions.sigmoid(-1) };
		double[] actualOutputs =
		{ 0.0 };
		double[][] weights =
		{
		{ 0, 1 } };
		NeuralNetworkBrain.computeLayerInPlace(inputs, actualOutputs, weights);
		Tester.lessOrEqual("Maximum value with random bias", actualOutputs[0], maximumExpectedOutputs[0]);
		Tester.greaterOrEqual("Minimum value with random bias", actualOutputs[0], minimumExpectedOutputs[0]);
	}

	@Test public void testComplexComputeLayerInPlace()
	{
		double[] inputs =
		{ 1.0, 0.5, 0.75, 1.0 };
		double[] maximumExpectedOutputs =
		{ Functions.sigmoid(1), Functions.sigmoid(2.25), Functions.sigmoid(3.5), Functions.sigmoid(5.25) };
		double[] minimumExpectedOutputs =
		{ Functions.sigmoid(-1), Functions.sigmoid(1.25), Functions.sigmoid(3.5), Functions.sigmoid(3.25) };
		double[] actualOutputs = new double[4];
		double[][] weights =
		{
		{ -1, -1, 1, 0, 0, 0 },
		{ 0.5, 0.5, 0, 1, 1, 0 },
		{ 1, 0, 0.5, 0, 2, 0.5 },
		{ 1, 1, 1, 1, 1, 1 } };
		NeuralNetworkBrain.computeLayerInPlace(inputs, actualOutputs, weights);
		Tester.lessOrEqual("1st output maximum value", actualOutputs[0], maximumExpectedOutputs[0]);
		Tester.greaterOrEqual("1st output minimum", actualOutputs[0], minimumExpectedOutputs[0]);
		Tester.lessOrEqual("2nd output maximum value", actualOutputs[1], maximumExpectedOutputs[1]);
		Tester.greaterOrEqual("2nd output minimum", actualOutputs[1], minimumExpectedOutputs[1]);
		Tester.lessOrEqual("3rd output maximum value", actualOutputs[2], maximumExpectedOutputs[2]);
		Tester.greaterOrEqual("3rd output minimum", actualOutputs[2], minimumExpectedOutputs[2]);
		Tester.lessOrEqual("4th output maximum value", actualOutputs[3], maximumExpectedOutputs[3]);
		Tester.greaterOrEqual("4th output minimum", actualOutputs[3], minimumExpectedOutputs[3]);
	}

	@Test public void validateWeights()
	{
		// A brain initialized with all zeros will have an empty weight matrix
		NeuralNetworkBrain brain = new NeuralNetworkBrain(0, 0, 0);
		Tester.isTrue("Validate a brand new Creature initialized with all zeros", brain.validateWeights(brain.getWeights()));

		// Test empty matrix on zero initialized brain
		Tester.isFalse("Null matrix doesn't validate", brain.validateWeights(null));
		double[][][] oneLayerEmpty =
		{};
		Tester.isFalse("One layer empty matrix doesn't validate for zero input/output brain", brain.validateWeights(oneLayerEmpty));
		double[][][] twoLayerEmpty =
		{
		{} };
		Tester.isTrue("Two layer empty matrix is still allowed", brain.validateWeights(twoLayerEmpty));
		double[][][] threeLayerEmpty =
		{
		{
		{} } };
		Tester.isFalse("Three layer empty matrix has too many outputs", brain.validateWeights(threeLayerEmpty));

		// Improperly initialized weight map still doesn't work
		double[][][] partiallyInitialized = new double[1][][];
		NullPointerException nullPointer = null;
		try
		{
			brain.validateWeights(partiallyInitialized);
		}
		catch(NullPointerException e)
		{
			nullPointer = e;
		}
		Tester.isNotNull("Exception is thrown with improperly initialized weights", nullPointer);

		// Change the shape of the brain and retest the empty matrix as a valid
		// weight
		// It should no longer validate
		brain.setOutputCount(1);
		Tester.isFalse("Empty matrix doesn't validate after setting output count", brain.validateWeights(twoLayerEmpty));
		Tester.isTrue("Matrix with only one vector will compute a single output", brain.validateWeights(new double[][][]
		{
		{
		{ 0, 0 } } }));

		brain.setInputCount(1);
		Tester.isTrue("Input = 1, Output = 2, Single Layer Weight Size is 1x3", brain.validateWeights(new double[][][]
		{
		{
		{ 0, 0, 1 } } }));
		Tester.isFalse("Matrix with insufficient single vector length fails", brain.validateWeights(new double[][][]
		{
		{
		{ 0 } } }));

		brain.setOutputCount(2);
		Tester.isTrue("Input = 1, Output = 2, Single Layer Weight Size is 2x3", brain.validateWeights(new double[][][]
		{
		{
		{ 0, 0, 1 },
		{ 1, 1, 0 } } }));
		Tester.isFalse("Input = 1, Output = 2, Single Layer Weight Size is irregular", brain.validateWeights(new double[][][]
		{
		{
		{ 0, 0 },
		{ 1, 1, 0 } } }));
		Tester.isFalse("Input = 1, Output = 2, Single Layer Weight Size is irregular", brain.validateWeights(new double[][][]
		{
		{
		{ 0, 0, 1 },
		{ 1, 1 } } }));
	}
}
