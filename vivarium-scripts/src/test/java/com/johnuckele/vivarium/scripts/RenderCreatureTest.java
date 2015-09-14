package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertTrue;

public class RenderCreatureTest
{
    public void test()
    {
        int worldSize = 10;
        // Create a world
        {
            String[] commandArgs = { "-o", "test.viv", "-s", "" + worldSize };
            CreateWorld.main(commandArgs);
        }
        // Extract the creature
        {
            String[] commandArgs = { "test.viv", "test.uck" };
            ExtractCreatureFromWorld.main(commandArgs);
        }
        // Render the creature
        {
            String[] commandArgs = { "test.uck" };
            RenderCreature.main(commandArgs);
        }
        assertTrue("Code did not crash", true);
    }
}
