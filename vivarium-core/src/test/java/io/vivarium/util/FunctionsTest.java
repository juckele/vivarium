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

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class FunctionsTest
{
    @Test
    public void testSigmoid()
    {
        Tester.equal("sigmoid(-1) = 0.2689", 0.2689, Functions.sigmoid(-1), 0.0001);

        Tester.equal("sigmoid(0) = 0.5", 0.5, Functions.sigmoid(0), 0.0001);

        Tester.equal("sigmoid(0.5) = 0.6224", 0.6224, Functions.sigmoid(0.5), 0.0001);

        Tester.equal("sigmoid(1) = 0.7310", 0.7310, Functions.sigmoid(1), 0.0001);

        Tester.equal("sigmoid(4) = 0.9820", 0.9820, Functions.sigmoid(4), 0.0001);
    }

    @Test
    public void testLogarithmicAverage()
    {
        double a, b, c;

        a = 1;
        b = 1;
        c = 1;
        Tester.equal("avg(1,1) = 1", Math.log(c), Functions.logarithmicAverage(Math.log(a), Math.log(b)), 0.0001);

        a = 3;
        b = 1;
        c = 2;
        Tester.equal("avg(3,1) = 2", Math.log(c), Functions.logarithmicAverage(Math.log(a), Math.log(b)), 0.0001);

        a = 2;
        b = 1;
        c = 1.5;
        Tester.equal("avg(2,1) = 1.5", Math.log(c), Functions.logarithmicAverage(Math.log(a), Math.log(b)), 0.0001);
    }
}
