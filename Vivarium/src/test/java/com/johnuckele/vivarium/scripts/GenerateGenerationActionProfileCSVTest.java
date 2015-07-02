package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.*;

import org.junit.Test;

public class GenerateGenerationActionProfileCSVTest
{
	@Test public void testWithKeepGenerationalActionProfiles()
	{
		// Create a world
		{
			String[] commandArgs =
			{ "test.viv", "25", "keepGenerationActionProfile", "true"};
			CreateWorld.main(commandArgs);
		}
		// Get the action profiles before doing any simulations in the new world
		{
			String[] commandArgs =
			{ "test.viv", "test.csv"};
			GenerateGenerationActionProfileCSV.main(commandArgs);
			assertTrue("census CSV file is well formatted", CSVUtils.validate("test.csv"));
			assertTrue("census CSV file has the correct number of rows", CSVUtils.getRowCount("test.csv") == 32);
		}
		// Run for a few thousand ticks
		{
			String[] commandArgs =
			{ "test.viv", "4000", "test.viv" };
			RunSimulation.main(commandArgs);
		}
		// Get the action profiles after running the world for a few thousand ticks
		{
			String[] commandArgs =
			{ "test.viv", "test.csv"};
			GenerateGenerationActionProfileCSV.main(commandArgs);
			assertTrue("census CSV file is well formatted", CSVUtils.validate("test.csv"));
			assertTrue("census CSV file has more rows", CSVUtils.getRowCount("test.csv") > 32);
		}
	}

	@Test public void testDefaultBehavior()
	{
		// Create a world without keeping action profiles
		{
			String[] commandArgs =
			{ "test.viv", "25"};
			CreateWorld.main(commandArgs);
		}
		// Get the action profiles, this should error out
		{
			String[] commandArgs =
			{ "test.viv", "test.csv"};
			try
			{
				GenerateGenerationActionProfileCSV.main(commandArgs);
				fail("generateGenerationActionProfileCSV should error out when no action profiles are available");
			}
			catch(Error e)
			{
				assertTrue("generateGenerationActionProfileCSV fails with errors", true);
			}
		}

	}

}
