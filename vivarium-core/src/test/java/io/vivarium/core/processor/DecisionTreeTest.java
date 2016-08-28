package io.vivarium.core.processor;

import java.util.Arrays;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;
import io.vivarium.util.Rand;

public class DecisionTreeTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testRandomConstruction()
    {
        DecisionTreeBlueprint blueprint = DecisionTreeBlueprint.makeDefault();
        blueprint.setMaximumDepth(4);
        DecisionTree processor = blueprint.makeProcessor(6, 6);
        double[] thresholds = processor.getThresholds();
        Tester.equal("Maximum depth of 4 should have 15 nodes", thresholds.length, 15);
        int[] indices = processor.getIndices();

        System.out.println("Thresholds: " + Arrays.toString(thresholds));
        System.out.println("Indices: " + Arrays.toString(indices));
        double[] inputs = new double[6];
        for (int i = 0; i < 6; i++)
        {
            inputs[i] = Rand.getInstance().getRandomPositiveDouble();
        }
        System.out.println("Inputs: " + Arrays.toString(inputs));
        double[] outputs = processor.outputs(inputs);
        System.out.println("Outputs: " + Arrays.toString(outputs));
        double outputSum = 0;
        for (int i = 0; i < outputs.length; i++)
        {
            outputSum += outputs[i];
        }
        Tester.equal("Sum of outputs should be equal to 1", outputSum, 1, 0.0001);
    }
}
