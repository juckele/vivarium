package com.johnuckele.vivarium.util;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class RandTest
{
	@Test public void testRandSeedForLCGMethods()
	{
		// Test with LCG based java.util.Random generated numbers
		// Set a seed
		Rand.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", Rand.getRandomPositiveDouble(), 0.7308781907032909, 0.0);
		Tester.equal("2nd random with seed of 1", Rand.getRandomPositiveDouble(), 0.41008081149220166, 0.0);
		Tester.equal("3rd random with seed of 1", Rand.getRandomPositiveDouble(), 0.20771484130971707, 0.0);
		Tester.equal("4th random with seed of 1", Rand.getRandomPositiveDouble(), 0.3327170559595112, 0.0);
		// Set the seed again
		Rand.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", Rand.getRandomPositiveDouble(), 0.7308781907032909, 0.0);
		// Clear the seed
		Rand.setRandomSeed();
		Tester.notEqual("1st random with generated seed", Rand.getRandomPositiveDouble(), 0.7308781907032909, 0.0);

		Rand.setRandomSeed(10);
		double randFromSeed1 = Rand.getRandomPositiveDouble();
		Rand.setRandomSeed(20);
		double randFromSeed2 = Rand.getRandomPositiveDouble();
		Tester.notEqual("Two randoms with different seeds don't match", randFromSeed1, randFromSeed2, 0.0);

		Rand.setRandomSeed(30);
		double randFromSeed3 = Rand.getRandomPositiveDouble();
		Rand.setRandomSeed(30);
		double randFromSeed4 = Rand.getRandomPositiveDouble();
		Tester.equal("Two randoms with matching seeds do match", randFromSeed3, randFromSeed4, 0.0);

		// Clear the seed
		Rand.setRandomSeed();
	}

	@Test public void testRandSeedForXorshiftMethods()
	{
		// Test with Xorshift based com.johnuckele.vivarium.core.UtilityFunctions generated numbers
		// Set a seed
		Rand.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", Rand.getRandomLong(), 35651601L);
		Tester.equal("2nd random with seed of 1", Rand.getRandomLong(), 1130297953386881L);
		Tester.equal("3rd random with seed of 1", Rand.getRandomLong(), -9204155794254196429L);
		Tester.equal("4th random with seed of 1", Rand.getRandomLong(), 144132848981442561L);
		// Set the seed again
		Rand.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", Rand.getRandomLong(), 35651601L);
		Tester.equal("2nd random with seed of 1", Rand.getRandomDouble(), 0.00012254714966179758, 0.0001);
		Tester.equal("3rd random with seed of 1", Rand.getRandomLong(), -9204155794254196429L);
		// Clear the seed
		Rand.setRandomSeed();
		Tester.notEqual("1st random with generated seed", Rand.getRandomLong(), 1130297953386881L);

		Rand.setRandomSeed(10);
		long randFromSeed1 = Rand.getRandomLong();
		Rand.setRandomSeed(20);
		long randFromSeed2 = Rand.getRandomLong();
		Tester.notEqual("Two randoms with different seeds don't match", randFromSeed1, randFromSeed2);

		Rand.setRandomSeed(30);
		long randFromSeed3 = Rand.getRandomLong();
		Rand.setRandomSeed(30);
		long randFromSeed4 = Rand.getRandomLong();
		Tester.equal("Two randoms with matching seeds do match", randFromSeed3, randFromSeed4);

		// Clear the seed
		Rand.setRandomSeed();
	}
	@Test public void testGetRandomPositiveDouble()
	{
		// Statistical distribution tests, all statistical tests are 4 sigma
		double fourSigmaZScore = 4;
		double allowedError = 0.01;
		int samples = (int) (Math.pow(fourSigmaZScore, 2) / ( 4 * Math.pow(allowedError, 2)));
		int heads = 0;
		for(int i = 0; i < samples; i++)
		{
			if( Rand.getRandomPositiveDouble() > 0.5 )
			{
				heads++;
			}
		}
		double observedProbability = ((double) heads) / samples;
		double error = Math.abs(observedProbability - 0.5);
		Tester.lessOrEqual("Above or below 0.5 should behave like an unbiased coin", error, allowedError);

		// Clear the seed
		Rand.setRandomSeed();
	}
	@Test public void testGetRandomLong()
	{
		// Statistical distribution tests, all statistical tests are 4 sigma
		double fourSigmaZScore = 4;
		double allowedError = 0.01;
		int samples = (int) (Math.pow(fourSigmaZScore, 2) / ( 4 * Math.pow(allowedError, 2)));
		int heads = 0;
		for(int i = 0; i < samples; i++)
		{
			if( Rand.getRandomLong() > 0 )
			{
				heads++;
			}
		}
		double observedProbability = ((double) heads) / samples;
		double error = Math.abs(observedProbability - 0.5);
		Tester.lessOrEqual("Above or below 0 should be an unbiased coin", error, allowedError);

		// Clear the seed
		Rand.setRandomSeed();
	}
}
