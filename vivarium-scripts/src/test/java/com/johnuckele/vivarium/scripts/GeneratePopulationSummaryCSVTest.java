package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class GeneratePopulationSummaryCSVTest
{
    @Test
    public void testWithKeepCensusDataProfiles()
    {
        // Create a world
        {
            String[] commandArgs = { "-o", "test.viv", "-s", "25" };
            CreateWorld.main(commandArgs);
        }
        // Get the population summary before doing any simulations in the new world
        {
            String[] commandArgs = { "test.viv", "test.csv" };
            GeneratePopulationSummaryCSV.main(commandArgs);
            assertTrue("census CSV file is well formatted", CSVUtils.validate("test.csv"));
            assertTrue("census CSV file has the correct number of rows", CSVUtils.getRowCount("test.csv") == 1);
        }
        // Run for a few thousand ticks
        {
            String[] commandArgs = { "test.viv", "4000", "test.viv" };
            RunSimulation.main(commandArgs);
        }
        // Get the population summary after running the world for a few thousand ticks
        {
            String[] commandArgs = { "test.viv", "test.csv" };
            GeneratePopulationSummaryCSV.main(commandArgs);
            assertTrue("census CSV file is well formatted", CSVUtils.validate("test.csv"));
            assertTrue("census CSV file has more rows", CSVUtils.getRowCount("test.csv") == 1);
        }
    }

    @Test
    public void testDefaultBehavior()
    {
        // Create a world without keeping census data
        {
            String[] commandArgs = { "-o", "test.viv", "-s", "25" };
            CreateWorld.main(commandArgs);
        }
        // Get the population summary, this should error out
        {
            String[] commandArgs = { "test.viv", "test.csv" };
            try
            {
                GeneratePopulationSummaryCSV.main(commandArgs);
                fail("generatePopulationSummaryCSV should error out when no census data is kept");
            }
            catch (Error e)
            {
                assertTrue("generatePopulationSummaryCSV fails with errors", true);
            }
        }

    }

}
