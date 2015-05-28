package com.johnuckele.vivarium.core;

import org.junit.Test;

import com.johnuckele.vivarium.util.Tester;

public class UtilityFunctionsTest
{
	@Test public void testRandSeedForLCGMethods()
	{
		// Test with LCG based java.util.Random generated numbers
		// Set a seed
		UtilityFunctions.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", UtilityFunctions.getRandomPositiveDouble(), 0.7308781907032909, 0.0);
		Tester.equal("2nd random with seed of 1", UtilityFunctions.getRandomPositiveDouble(), 0.41008081149220166, 0.0);
		Tester.equal("3rd random with seed of 1", UtilityFunctions.getRandomPositiveDouble(), 0.20771484130971707, 0.0);
		Tester.equal("4th random with seed of 1", UtilityFunctions.getRandomPositiveDouble(), 0.3327170559595112, 0.0);
		// Set the seed again
		UtilityFunctions.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", UtilityFunctions.getRandomPositiveDouble(), 0.7308781907032909, 0.0);
		// Clear the seed
		UtilityFunctions.setRandomSeed();
		Tester.notEqual("1st random with generated seed", UtilityFunctions.getRandomPositiveDouble(), 0.7308781907032909, 0.0);

		UtilityFunctions.setRandomSeed(10);
		double randFromSeed1 = UtilityFunctions.getRandomPositiveDouble();
		UtilityFunctions.setRandomSeed(20);
		double randFromSeed2 = UtilityFunctions.getRandomPositiveDouble();
		Tester.notEqual("Two randoms with different seeds don't match", randFromSeed1, randFromSeed2, 0.0);

		UtilityFunctions.setRandomSeed(30);
		double randFromSeed3 = UtilityFunctions.getRandomPositiveDouble();
		UtilityFunctions.setRandomSeed(30);
		double randFromSeed4 = UtilityFunctions.getRandomPositiveDouble();
		Tester.equal("Two randoms with matching seeds do match", randFromSeed3, randFromSeed4, 0.0);
	}

	@Test public void testRandSeedForXorshiftMethods()
	{
		// Test with Xorshift based com.johnuckele.vivarium.core.UtilityFunctions generated numbers
		// Set a seed
		UtilityFunctions.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", UtilityFunctions.getRandomLong(), 35651601L);
		Tester.equal("2nd random with seed of 1", UtilityFunctions.getRandomLong(), 1130297953386881L);
		Tester.equal("3rd random with seed of 1", UtilityFunctions.getRandomLong(), -9204155794254196429L);
		Tester.equal("4th random with seed of 1", UtilityFunctions.getRandomLong(), 144132848981442561L);
		// Set the seed again
		UtilityFunctions.setRandomSeed(1);
		Tester.equal("1st random with seed of 1", UtilityFunctions.getRandomLong(), 35651601L);
		Tester.equal("2nd random with seed of 1", UtilityFunctions.getRandomDouble(), 0.00012254714966179758, 0.0001);
		Tester.equal("3rd random with seed of 1", UtilityFunctions.getRandomLong(), -9204155794254196429L);
		// Clear the seed
		UtilityFunctions.setRandomSeed();
		Tester.notEqual("1st random with generated seed", UtilityFunctions.getRandomLong(), 1130297953386881L);
		
		UtilityFunctions.setRandomSeed(10);
		long randFromSeed1 = UtilityFunctions.getRandomLong();
		UtilityFunctions.setRandomSeed(20);
		long randFromSeed2 = UtilityFunctions.getRandomLong();
		Tester.notEqual("Two randoms with different seeds don't match", randFromSeed1, randFromSeed2);

		UtilityFunctions.setRandomSeed(30);
		long randFromSeed3 = UtilityFunctions.getRandomLong();
		UtilityFunctions.setRandomSeed(30);
		long randFromSeed4 = UtilityFunctions.getRandomLong();
		Tester.equal("Two randoms with matching seeds do match", randFromSeed3, randFromSeed4);
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
			if( UtilityFunctions.getRandomPositiveDouble() > 0.5 )
			{
				heads++;
			}
		}
		double observedProbability = ((double) heads) / samples;
		double error = Math.abs(observedProbability - 0.5);
		Tester.lessOrEqual("Above or below 0.5 should behave like an unbiased coin", error, allowedError);
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
			if( UtilityFunctions.getRandomLong() > 0 )
			{
				heads++;
			}
		}
		double observedProbability = ((double) heads) / samples;
		double error = Math.abs(observedProbability - 0.5);
		Tester.lessOrEqual("Above or below 0 should be an unbiased coin", error, allowedError);
	}
	@Test public void testSigmoid()
	{
		Tester.equal("sigmoid(-1) = 0.2689",
				0.2689,
				UtilityFunctions.sigmoid(-1),
				0.0001
			);

		Tester.equal("sigmoid(0) = 0.5",
			0.5,
			UtilityFunctions.sigmoid(0),
			0.0001
		);

		Tester.equal("sigmoid(0.5) = 0.6224",
			0.6224,
			UtilityFunctions.sigmoid(0.5),
			0.0001
		);

		Tester.equal("sigmoid(1) = 0.7310",
				0.7310,
				UtilityFunctions.sigmoid(1),
				0.0001
			);

		Tester.equal("sigmoid(4) = 0.9820",
				0.9820,
				UtilityFunctions.sigmoid(4),
				0.0001
			);
	}
			
	@Test public void testLogarithmicAverage()
	{
		double a, b, c;

		a = 1; b = 1; c = 1;
		Tester.equal(
			"avg(1,1) = 1",
			Math.log(c),
			UtilityFunctions.logarithmicAverage(Math.log(a), Math.log(b)),
			0.0001
		);

		a = 3; b = 1; c = 2;
		Tester.equal(
			"avg(3,1) = 2",
			Math.log(c),
			UtilityFunctions.logarithmicAverage(Math.log(a), Math.log(b)),
			0.0001
		);

		a = 2; b = 1; c = 1.5;
		Tester.equal(
			"avg(2,1) = 1.5",
			Math.log(c),
			UtilityFunctions.logarithmicAverage(Math.log(a), Math.log(b)),
			0.0001
		);
	}

}
