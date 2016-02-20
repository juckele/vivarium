package io.vivarium.scripts;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import com.johnuckele.vtest.Tester;

import io.vivarium.test.FastTest;
import io.vivarium.test.SystemTest;

public class RunSimulationTest
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
    public void testDefaultWithWorld()
    {
        {
            String[] commandArgs = { "-o", path + "w.viv" };
            CreateWorld.main(commandArgs);
        }
        {
            String[] commandArgs = { "-i", path + "w.viv", "-o", path + "w2.viv", "-t", "5000" };
            RunSimulation.main(commandArgs);
        }
    }

    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testTimeLimit()
    {
        {
            String[] commandArgs = { "-o", path + "w.viv" };
            CreateWorld.main(commandArgs);
        }
        long start = System.currentTimeMillis();
        {
            String[] commandArgs = { "-i", path + "w.viv", "-o", path + "w2.viv", "-s", "1" };
            RunSimulation.main(commandArgs);
        }
        long end = System.currentTimeMillis();
        Tester.greaterOrEqual("World Simulation should take at least one second", (end - start) / 1000.0, 1, 0.0);
        Tester.lessOrEqual("World Simulation should be less than two seconds", (end - start) / 1000.0, 2, 0.0);
    }

    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testTimeLimitAndTickLimit()
    {
        {
            String[] commandArgs = { "-o", path + "w.viv" };
            CreateWorld.main(commandArgs);
        }
        long start = System.currentTimeMillis();
        {
            String[] commandArgs = { "-i", path + "w.viv", "-o", path + "s2.viv", "-s", "1", "-t", "1" };
            RunSimulation.main(commandArgs);
        }
        long end = System.currentTimeMillis();
        Tester.lessThan("World Simulation should take less than a second", (end - start) / 1000.0, 1, 0.0);
    }
}
