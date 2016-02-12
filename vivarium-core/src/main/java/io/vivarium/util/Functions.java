/*
 * The MIT License (MIT)
 *
 * Copyright © 2015 John H. Uckele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright
 * notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.vivarium.util;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

public class Functions
{
    /**
     * computes the logistic sigmoid of a value, the logistic sigmoid is s(x) = 1 / ( 1 + e ^ -x )
     *
     * @param x
     * @return sigmoid(x)
     */
    public static double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }

    /**
     * computes the midpoint between two values on a logarithmic scale, defined as log((exp(A)+exp(B))/2), but is usable
     * even when A or B are too large to fit into Java primitives.
     *
     * @param logA
     *            the log of A
     * @param logB
     *            the log of B
     * @return log((A+B)/2)
     */
    public static double logarithmicAverage(double logA, double logB)
    {
        double difference = Math.abs(logA - logB);
        double expandedDifference = Math.exp(difference);
        return Math.log((expandedDifference + 1) / 2) + Math.min(logA, logB);
    }

    /**
     * computes an ordered array such that each element in the array is as far as possible from all previous elements.
     * In a grid search style application, this allows results to be generated at a low granularity, and over time to
     * generate additional higher granularity data points.
     *
     * For example, the inputs of min = 1, max = 10, and steps = 10 will produce the output roughly equivalent to {1.0,
     * 10.0, 5.0, 3.0, 7.0, 2.0, 4.0, 6.0, 8.0, 9.0}. The order of outputs is not guaranteed, but maximal gaps between
     * preceeding values should always be observed.
     *
     * @param min
     *            The minimum value in the dither array. This will always be returned as the first element.
     * @param max
     *            The maximum value in the dither array. This will always be returned as the second element.
     * @param steps
     *            The total number of elements in the dither array. This value must be at least 2.
     * @return The dither array.
     */
    public static double[] generateDitherArray(double min, double max, int steps)
    {
        Preconditions.checkArgument(steps >= 2);
        Preconditions.checkArgument(min < max);

        // Build the basic data structures
        double[] ditherArray = new double[steps];
        double stepSize = (max - min) / (steps - 1);
        int currentStepResolution = steps / 2;

        // Immediately place the min value into the ditherArray and add it to the used values
        Set<Integer> usedValues = new HashSet<>();
        usedValues.add(0);
        ditherArray[0] = min;
        ditherArray[1] = max;

        // Build the unusedValues, which should initially contain everything but value 0.
        Set<Integer> unusedValues = new HashSet<>();
        for (int i = 2; i < steps; i++)
        {
            unusedValues.add(i);
        }

        // Build the dither array, starting at the 3rd element (first and second element are always min and max)
        int ditherArrayIndex = 2;
        // While the resolution for steps is larger than 1, step based on step size from used values
        while (currentStepResolution > 1)
        {
            // Track what we use for each pass based on step size, to move from unused to used values at the end
            Set<Integer> newValues = new HashSet<>();

            // For each used value, find the next value based on step size.
            for (int usedValue : usedValues)
            {
                int newValue = usedValue + currentStepResolution;
                newValues.add(newValue);
                ditherArray[ditherArrayIndex++] = newValue * stepSize + min;
            }

            // After finding all new values, move all new values from unused to used values.
            // Note: There is no need to check whether unusedValues contains any of the values we're seeing yet, because
            // the step size shrinks faster than we can get collisions.
            for (int newValue : newValues)
            {
                unusedValues.remove(newValue);
                usedValues.add(newValue);
            }

            // At the end of the pass for this step size, halve the step size (rounding down).
            currentStepResolution /= 2;
        }
        // After the step size resolution shrinks to 1, we just want to immediately go through all unused values and
        // add them.
        for (int unusedValue : unusedValues)
        {
            ditherArray[ditherArrayIndex++] = unusedValue * stepSize + min;
        }

        return ditherArray;
    }
}
