package com.johnuckele.vivarium;

public class UtilityFunctions
{
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
