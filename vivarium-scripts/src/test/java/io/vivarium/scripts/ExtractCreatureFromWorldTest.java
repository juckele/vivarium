package io.vivarium.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.vivarium.core.Creature;

public class ExtractCreatureFromWorldTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public String path;

    @Before
    public void setupPath() throws IOException
    {
        path = folder.getRoot().getCanonicalPath() + File.separator;
    }

    @Test
    public void test()
    {
        int worldSize = 10;
        // Create a world
        {
            String[] commandArgs = { "-o", path + "w.viv", "-s", "" + worldSize };
            CreateWorld.main(commandArgs);
        }
        // Extract the creature
        {
            String[] commandArgs = { "-w", path + "w.viv", "-o", path + "c.viv" };
            ExtractCreatureFromWorld.main(commandArgs);
        }
        Creature u = (Creature) ScriptIO.loadObject(path + "c.viv", Format.JSON);
        assertNotNull("Creature is loaded from file correctly", u);
        assertNotNull("Creature has brain object correctly reloaded", u.getBrain());
        assertTrue("Creature has reasonable ID", u.getID() >= 0 && u.getID() < (worldSize - 2) * (worldSize - 2));
        assertEquals("Creature should have zero age on fresh creation", u.getAge(), 0);
        // Run for a few hundred ticks
        {
            String[] commandArgs = { "-i", path + "w.viv", "-t", "200", "-o", path + "w.viv" };
            RunSimulation.main(commandArgs);
        }
        // Extract the creature again
        {
            String[] commandArgs = { "-w", path + "w.viv", "-o", path + "c.viv" };
            ExtractCreatureFromWorld.main(commandArgs);
        }
        u = (Creature) ScriptIO.loadObject(path + "c.viv", Format.JSON);
        assertEquals("Creature age should match age of world", u.getAge(), 200);
    }
}
