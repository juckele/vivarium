package com.johnuckele.vivarium.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilityFunctionsTest
{
	@Test public void testLogarithmicAverage()
	{
		double a, b, c;

		a = 1; b = 1; c = 1;
		assertEquals(
			"avg(1,1) = 1",
			Math.log(c),
			UtilityFunctions.logarithmicAverage(Math.log(a), Math.log(b)),
			0.0001
		);

		a = 3; b = 1; c = 2;
		assertEquals(
			"avg(3,1) = 2",
			Math.log(c),
			UtilityFunctions.logarithmicAverage(Math.log(a), Math.log(b)),
			0.0001
		);

		a = 2; b = 1; c = 1.5;
		assertEquals(
			"avg(2,1) = 1.5",
			Math.log(c),
			UtilityFunctions.logarithmicAverage(Math.log(a), Math.log(b)),
			0.0001
		);
	}

}
