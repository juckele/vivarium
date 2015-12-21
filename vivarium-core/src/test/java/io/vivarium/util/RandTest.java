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

public class RandTest
{
    @Test
    public void testRandSeedForLCGMethods()
    {
        // Test with LCG based java.util.Random generated numbers
        // Set a seed
        Rand.getInstance().setRandomSeed(1);
        Tester.equal("1st random with seed of 1", Rand.getInstance().getRandomPositiveDouble(), 0.7308781907032909,
                0.0);
        Tester.equal("2nd random with seed of 1", Rand.getInstance().getRandomPositiveDouble(), 0.41008081149220166,
                0.0);
        Tester.equal("3rd random with seed of 1", Rand.getInstance().getRandomPositiveDouble(), 0.20771484130971707,
                0.0);
        Tester.equal("4th random with seed of 1", Rand.getInstance().getRandomPositiveDouble(), 0.3327170559595112,
                0.0);
        // Set the seed again
        Rand.getInstance().setRandomSeed(1);
        Tester.equal("1st random with seed of 1", Rand.getInstance().getRandomPositiveDouble(), 0.7308781907032909,
                0.0);
        // Clear the seed
        Rand.getInstance().setRandomSeed();
        Tester.notEqual("1st random with generated seed", Rand.getInstance().getRandomPositiveDouble(),
                0.7308781907032909, 0.0);

        Rand.getInstance().setRandomSeed(10);
        double randFromSeed1 = Rand.getInstance().getRandomPositiveDouble();
        Rand.getInstance().setRandomSeed(20);
        double randFromSeed2 = Rand.getInstance().getRandomPositiveDouble();
        Tester.notEqual("Two randoms with different seeds don't match", randFromSeed1, randFromSeed2, 0.0);

        Rand.getInstance().setRandomSeed(30);
        double randFromSeed3 = Rand.getInstance().getRandomPositiveDouble();
        Rand.getInstance().setRandomSeed(30);
        double randFromSeed4 = Rand.getInstance().getRandomPositiveDouble();
        Tester.equal("Two randoms with matching seeds do match", randFromSeed3, randFromSeed4, 0.0);

        // Clear the seed
        Rand.getInstance().setRandomSeed();
    }

    @Test
    public void testRandSeedForXorshiftMethods()
    {
        // Test with Xorshift based
        // io.vivarium.core.UtilityFunctions generated numbers
        // Set a seed
        Rand.getInstance().setRandomSeed(1);
        Tester.equal("1st random with seed of 1", Rand.getInstance().getRandomLong(), 35651601L);
        Tester.equal("2nd random with seed of 1", Rand.getInstance().getRandomLong(), 1130297953386881L);
        Tester.equal("3rd random with seed of 1", Rand.getInstance().getRandomLong(), -9204155794254196429L);
        Tester.equal("4th random with seed of 1", Rand.getInstance().getRandomLong(), 144132848981442561L);
        // Set the seed again
        Rand.getInstance().setRandomSeed(1);
        Tester.equal("1st random with seed of 1", Rand.getInstance().getRandomLong(), 35651601L);
        Tester.equal("2nd random with seed of 1", Rand.getInstance().getRandomDouble(), 0.00012254714966179758, 0.0001);
        Tester.equal("3rd random with seed of 1", Rand.getInstance().getRandomLong(), -9204155794254196429L);
        // Clear the seed
        Rand.getInstance().setRandomSeed();
        Tester.notEqual("1st random with generated seed", Rand.getInstance().getRandomLong(), 1130297953386881L);

        Rand.getInstance().setRandomSeed(10);
        long randFromSeed1 = Rand.getInstance().getRandomLong();
        Rand.getInstance().setRandomSeed(20);
        long randFromSeed2 = Rand.getInstance().getRandomLong();
        Tester.notEqual("Two randoms with different seeds don't match", randFromSeed1, randFromSeed2);

        Rand.getInstance().setRandomSeed(30);
        long randFromSeed3 = Rand.getInstance().getRandomLong();
        Rand.getInstance().setRandomSeed(30);
        long randFromSeed4 = Rand.getInstance().getRandomLong();
        Tester.equal("Two randoms with matching seeds do match", randFromSeed3, randFromSeed4);

        // Clear the seed
        Rand.getInstance().setRandomSeed();
    }

    @Test
    public void testGetRandomPositiveDouble()
    {
        // Statistical distribution tests, all statistical tests are 4 sigma
        double fourSigmaZScore = 4;
        double allowedError = 0.01;
        int samples = (int) (Math.pow(fourSigmaZScore, 2) / (4 * Math.pow(allowedError, 2)));
        int heads = 0;
        for (int i = 0; i < samples; i++)
        {
            if (Rand.getInstance().getRandomPositiveDouble() > 0.5)
            {
                heads++;
            }
        }
        double observedProbability = ((double) heads) / samples;
        double error = Math.abs(observedProbability - 0.5);
        Tester.lessOrEqual("Above or below 0.5 should behave like an unbiased coin", error, allowedError);

        // Clear the seed
        Rand.getInstance().setRandomSeed();
    }

    @Test
    public void testGetRandomLong()
    {
        // Statistical distribution tests, all statistical tests are 4 sigma
        double fourSigmaZScore = 4;
        double allowedError = 0.01;
        int samples = (int) (Math.pow(fourSigmaZScore, 2) / (4 * Math.pow(allowedError, 2)));
        int heads = 0;
        for (int i = 0; i < samples; i++)
        {
            if (Rand.getInstance().getRandomLong() > 0)
            {
                heads++;
            }
        }
        double observedProbability = ((double) heads) / samples;
        double error = Math.abs(observedProbability - 0.5);
        Tester.lessOrEqual("Above or below 0 should be an unbiased coin", error, allowedError);

        // Clear the seed
        Rand.getInstance().setRandomSeed();
    }
}
