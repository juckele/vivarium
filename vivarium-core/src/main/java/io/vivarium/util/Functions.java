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
}
