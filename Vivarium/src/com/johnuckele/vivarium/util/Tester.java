package com.johnuckele.vivarium.util;

import static org.junit.Assert.assertTrue;

public class Tester
{
	private static String classScope = "";
	private static String methodScope = "";

	private static void checkScope()
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		String localClassScope = elements[3].getClassName();
		String localMethodScope = elements[3].getMethodName();
		// If either have changed, produce some verbose output
		if(!classScope.equals(localClassScope))
		{
			classScope = localClassScope;
			System.out.println("Starting tests for "+localClassScope);
		}
		if(!methodScope.equals(localMethodScope))
		{
			methodScope = localMethodScope;
			System.out.println("\t"+methodScope);
		}
	}

	public static void equal(String message, double result, double expected, double margin)
	{
		checkScope();
		System.out.println("\t\t"+message+": "+result+" == "+expected+" +/- "+margin);
		double difference = result - expected; 
		test(Math.abs(difference) <= margin);
	}

	public static void equal(String message, long result, long expected)
	{
		checkScope();
		System.out.println("\t\t"+message+": "+result+" == "+expected);
		test( result == expected );
	}

	public static void fail(String message)
	{
		checkScope();
		System.out.println("\t\t"+message);
		test( false );
	}

	public static void greaterOrEqual(String message, double result, double expected)
	{
		checkScope();
		System.out.println("\t\t"+message+": "+result+" >= "+expected);
		test( result >= expected );
	}

	public static void isFalse(String string, boolean test)
	{
		checkScope();
		System.out.println("\t\t"+string+": "+test);
		test( !test );
	}

	public static void isNotNull(String string, Object o)
	{
		checkScope();
		System.out.println("\t\t"+string+": "+o);
		test( o != null );
	}

	public static void isTrue(String string, boolean test)
	{
		checkScope();
		System.out.println("\t\t"+string+": "+test);
		test( test );
	}

	public static void lessOrEqual(String message, double result, double expected)
	{
		checkScope();
		System.out.println("\t\t"+message+": "+result+" <= "+expected);
		test( result <= expected );
	}

	public static void notEqual(String message, double result, double expected, double margin)
	{
		checkScope();
		System.out.println("\t\t"+message+": "+result+" != "+expected+" +/- "+margin);
		double difference = result - expected; 
		test(Math.abs(difference) > margin);
	}

	public static void notEqual(String message, long result, long expected)
	{
		checkScope();
		System.out.println("\t\t"+message+": "+result+" != "+expected);
		test( result != expected );
	}

	private static void test(Boolean evaluation)
	{
		if( evaluation )
		{
			System.out.println("\t\t\t✓ PASSED");
			assertTrue(true);
		}
		else
		{
			System.out.println("\t\t\t✗ FAILED");
			assertTrue(false);
		}
	}
}
