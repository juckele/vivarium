package com.johnuckele.vivarium.scripts;

import org.junit.Test;

public class RunSimulationTest
{
    @Test
    public void testDefault()
    {
        {
            String[] commandArgs = { "-o", "/tmp/w.viv" };
            CreateWorld.main(commandArgs);
        }
        {
            String[] commandArgs = { "-i", "/tmp/w.viv", "-o", "/tmp/w2.viv", "-t", "5000" };
            RunSimulation.main(commandArgs);
        }
    }
}
