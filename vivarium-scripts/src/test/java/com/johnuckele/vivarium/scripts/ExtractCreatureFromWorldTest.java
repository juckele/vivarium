package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.johnuckele.vivarium.core.Creature;

public class ExtractCreatureFromWorldTest
{
    // TODO FIX AUDIT FUNCTIONS
    public void test()
    {
        int worldSize = 10;
        // Create a world
        {
            String[] commandArgs = { "test.viv", "" + worldSize };
            CreateWorld.main(commandArgs);
        }
        // Extract the creature
        {
            String[] commandArgs = { "test.viv", "test.uck" };
            ExtractCreatureFromWorld.main(commandArgs);
        }
        Creature u = ScriptIO.loadCreature("test.uck", Format.JAVA_SERIALIZABLE);
        assertNotNull("Creature is loaded from file correctly", u);
        assertNotNull("Creature has brain object correctly reloaded", u.getBrain());
        assertTrue("Creature has reasonable ID", u.getID() >= 0 && u.getID() < (worldSize - 2) * (worldSize - 2));
        assertEquals("Creature should have zero age on fresh creation", u.getAge(), 0);
        // Run for a few hundred ticks
        {
            String[] commandArgs = { "test.viv", "200", "test.viv" };
            RunSimulation.main(commandArgs);
        }
        // Extract the creature again
        {
            String[] commandArgs = { "test.viv", "test.uck" };
            ExtractCreatureFromWorld.main(commandArgs);
        }
        u = ScriptIO.loadCreature("test.uck", Format.JAVA_SERIALIZABLE);
        assertEquals("Creature age should match age of world", u.getAge(), 200);
    }
}
