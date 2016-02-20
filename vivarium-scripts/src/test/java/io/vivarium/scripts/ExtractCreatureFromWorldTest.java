package io.vivarium.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import io.vivarium.core.Creature;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.test.FastTest;
import io.vivarium.test.SystemTest;

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
    @Category({ FastTest.class, SystemTest.class })
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
        Creature u = FileIO.loadObjectCollection(path + "c.viv", Format.JSON).getFirst(Creature.class);
        assertNotNull("Creature is loaded from file correctly", u);
        assertNotNull("Creature has processor object correctly reloaded", u.getProcessor());
        assertTrue("Creature has reasonable ID: " + u.getID(),
                u.getID() >= 0 && u.getID() < (worldSize - 2) * (worldSize - 2));
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
        u = FileIO.loadObjectCollection(path + "c.viv", Format.JSON).getFirst(Creature.class);
        assertEquals("Creature age should match age of world", u.getAge(), 200);
    }
}
