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
     * For example, the inputs of min = 1, max = 10, and steps = 10 will produce the output {1.0, 10.0, 5.0, 3.0, 7.0,
     * 2.0, 4.0, 6.0, 8.0, 9.0}
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

        double[] ditherArray = new double[steps];
        double stepSize = (max - min) / (steps - 1);

        ditherArray[0] = min;
        ditherArray[1] = max;
        for (int outerIndex = 2; outerIndex < ditherArray.length; outerIndex++)
        {
            double targetValue = 0;
            double maximumDifference = Double.MIN_VALUE;
            for (int trialStep = 1; trialStep < steps; trialStep++)
            {
                double trialValue = min + trialStep * stepSize;
                double minimumDifference = Double.MAX_VALUE;
                for (int innerIndex = 0; innerIndex < outerIndex; innerIndex++)
                {
                    double difference = Math.abs(ditherArray[innerIndex] - trialValue);
                    if (difference < minimumDifference)
                    {
                        minimumDifference = difference;
                    }
                }
                if (minimumDifference > maximumDifference)
                {
                    targetValue = trialValue;
                    maximumDifference = minimumDifference;
                }
            }
            ditherArray[outerIndex] = targetValue;
        }

        return ditherArray;
    }

}
