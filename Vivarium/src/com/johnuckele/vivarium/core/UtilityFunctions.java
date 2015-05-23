package com.johnuckele.vivarium.core;

public class UtilityFunctions
{
	/**
	 * computes the logistic sigmoid of a value,
	 * the logistic sigmoid is s(x) = 1 / ( 1 + e ^ -x )
	 * 
	 * @param x
	 * @return sigmoid(x)
	 */
	public static double sigmoid(double x)
	{
		return 1 / (1 + Math.exp(-x));
	}

	/**
	 * computes the midpoint between two values on a logarithmic
	 * scale, defined as log((exp(A)+exp(B))/2), but is usable
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
