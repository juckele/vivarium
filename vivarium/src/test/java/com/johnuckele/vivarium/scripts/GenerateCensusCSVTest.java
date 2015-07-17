package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class GenerateCensusCSVTest
{
	@Test public void testWithKeepCensusData()
	{
		// Create a world
		{
			String[] commandArgs =
			{ "test.viv", "25", "keepCensusData", "true"};
			CreateWorld.main(commandArgs);
		}
		// Get the census before doing any simulations in the new world
		{
			String[] commandArgs =
			{ "test.viv", "test.csv"};
			GenerateCensusCSV.main(commandArgs);
			assertTrue("census CSV file is well formatted", CSVUtils.validate("test.csv"));
			assertTrue("census CSV file has the correct number of rows", CSVUtils.getRowCount("test.csv") == 1);
		}
		// Run for a few thousand ticks
		{
			String[] commandArgs =
			{ "test.viv", "4000", "test.viv" };
			RunSimulation.main(commandArgs);
		}
		// Get the census after running the world for a few thousand ticks
		{
			String[] commandArgs =
			{ "test.viv", "test.csv"};
			GenerateCensusCSV.main(commandArgs);
			assertTrue("census CSV file is well formatted", CSVUtils.validate("test.csv"));
			assertTrue("census CSV file has more rows", CSVUtils.getRowCount("test.csv") > 1);
		}
	}

	@Test public void testDefaultBehavior()
	{
		// Create a world without a census
		{
			String[] commandArgs =
			{ "test.viv", "25"};
			CreateWorld.main(commandArgs);
		}
		// Get the census, this should error out
		{
			String[] commandArgs =
			{ "test.viv", "test.csv"};
			try
			{
				GenerateCensusCSV.main(commandArgs);
				fail("generateCensusCSV should error out when no census is available");
			}
			catch(Error e)
			{
				assertTrue("generateCensusCSV fails with errors", true);
			}
		}

	}

}
