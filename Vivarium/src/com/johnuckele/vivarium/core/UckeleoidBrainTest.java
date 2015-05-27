package com.johnuckele.vivarium.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UckeleoidBrainTest
{
	@Test public void testComputeLayerInPlace()
	{
		double[] inputs = {};
		double[] expectedOutputs = {UtilityFunctions.sigmoid(1)};
		double[] actualOutputs = {0.0};
		double[][] weights = {{1, 0}};
		UckeleoidBrain.computeLayerInPlace(inputs, actualOutputs, weights);
		assertEquals("no inputs, output is = constant bias", expectedOutputs[0], actualOutputs[0], 0.0001);
	}

}
