/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core.processor;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.util.Functions;

public class NeuralNetworkTest
{
    @Test
    public void testNormalizeWeights()
    {
        double length = Math.sqrt(7 * 6);
        NeuralNetwork nn = new NeuralNetwork(7, 6, true, length);
        Tester.equal("Random initialized network should match the initial target length", nn.getGenomeLength(), length,
                0.01);
        nn.normalizeWeights(1);
        Tester.equal("Normalized network should match the new target length", nn.getGenomeLength(), 1, 0.01);
        nn.normalizeWeights(length);
        Tester.equal("Normalized network should match the new target length", nn.getGenomeLength(), length, 0.01);
    }

    @Test
    public void testComputeLayerInPlaceConstantBias()
    {
        double[] inputs = {};
        double[] expectedOutputs = { Functions.sigmoid(1) };
        double[] actualOutputs = { 0.0 };
        double[][] weights = { { 1, 0 } };
        NeuralNetwork.computeLayerInPlace(inputs, actualOutputs, weights);
        Tester.equal("no inputs, output is = constant bias", actualOutputs[0], expectedOutputs[0], 0.0);
    }

    @Test
    public void testComputeLayerInPlaceWithoutBias()
    {
        double[] inputs = { 1.0, 0.5, 0.0 };
        double[] expectedOutputs = { Functions.sigmoid(1.5), Functions.sigmoid(2), Functions.sigmoid(-1), 0.5 };
        double[] actualOutputs = new double[4];
        double[][] weights = { { 0, 0, 1, 1, 1 }, { 0, 0, 2, 0, 0 }, { 0, 0, -2, 2, 0 }, { 0, 0, 0, 0, 1 } };
        NeuralNetwork.computeLayerInPlace(inputs, actualOutputs, weights);
        Tester.equal("3 inputs, 1st output", actualOutputs[0], expectedOutputs[0], 0.0);
        Tester.equal("3 inputs, 2nd output", actualOutputs[1], expectedOutputs[1], 0.0);
        Tester.equal("3 inputs, 3rd output", actualOutputs[2], expectedOutputs[2], 0.0);
        Tester.equal("3 inputs, 4th output", actualOutputs[3], expectedOutputs[3], 0.0);
    }

    @Test
    public void testComputeLayerInPlaceWithRandomBias()
    {
        double[] inputs = {};
        double[] maximumExpectedOutputs = { Functions.sigmoid(1) };
        double[] minimumExpectedOutputs = { Functions.sigmoid(-1) };
        double[] actualOutputs = { 0.0 };
        double[][] weights = { { 0, 1 } };
        NeuralNetwork.computeLayerInPlace(inputs, actualOutputs, weights);
        Tester.lessOrEqual("Maximum value with random bias", actualOutputs[0], maximumExpectedOutputs[0]);
        Tester.greaterOrEqual("Minimum value with random bias", actualOutputs[0], minimumExpectedOutputs[0]);
    }

    @Test
    public void testComplexComputeLayerInPlace()
    {
        double[] inputs = { 1.0, 0.5, 0.75, 1.0 };
        double[] maximumExpectedOutputs = { Functions.sigmoid(1), Functions.sigmoid(2.25), Functions.sigmoid(3.5),
                Functions.sigmoid(5.25) };
        double[] minimumExpectedOutputs = { Functions.sigmoid(-1), Functions.sigmoid(1.25), Functions.sigmoid(3.5),
                Functions.sigmoid(3.25) };
        double[] actualOutputs = new double[4];
        double[][] weights = { { -1, -1, 1, 0, 0, 0 }, { 0.5, 0.5, 0, 1, 1, 0 }, { 1, 0, 0.5, 0, 2, 0.5 },
                { 1, 1, 1, 1, 1, 1 } };
        NeuralNetwork.computeLayerInPlace(inputs, actualOutputs, weights);
        Tester.lessOrEqual("1st output maximum value", actualOutputs[0], maximumExpectedOutputs[0]);
        Tester.greaterOrEqual("1st output minimum", actualOutputs[0], minimumExpectedOutputs[0]);
        Tester.lessOrEqual("2nd output maximum value", actualOutputs[1], maximumExpectedOutputs[1]);
        Tester.greaterOrEqual("2nd output minimum", actualOutputs[1], minimumExpectedOutputs[1]);
        Tester.lessOrEqual("3rd output maximum value", actualOutputs[2], maximumExpectedOutputs[2]);
        Tester.greaterOrEqual("3rd output minimum", actualOutputs[2], minimumExpectedOutputs[2]);
        Tester.lessOrEqual("4th output maximum value", actualOutputs[3], maximumExpectedOutputs[3]);
        Tester.greaterOrEqual("4th output minimum", actualOutputs[3], minimumExpectedOutputs[3]);
    }
}
