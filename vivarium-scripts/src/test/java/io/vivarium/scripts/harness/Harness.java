package io.vivarium.scripts.harness;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import io.vivarium.scripts.CreateAuditBlueprint;
import io.vivarium.scripts.CreateWorldBlueprint;
import io.vivarium.scripts.CreateCreatureBlueprint;
import io.vivarium.scripts.CreateWorld;
import io.vivarium.scripts.RenderWorld;
import io.vivarium.scripts.RunSimulation;
import io.vivarium.test.FastTest;
import io.vivarium.test.SystemTest;

public class Harness
{
    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testWorldBuildWorkflow()
    {
        {
            String[] commandArgs = { "-o", "/tmp/af.viv", "-t", "CENSUS" };
            CreateAuditBlueprint.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/s.viv", "maximumFood", "3000" };
            CreateCreatureBlueprint.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/bp.viv", "-a", "/tmp/af.viv" };
            CreateWorldBlueprint.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/bp.viv", "-a", "/tmp/af.viv", "-s", "/tmp/s.viv" };
            CreateWorldBlueprint.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/w.viv", "-b", "/tmp/bp.viv" };
            CreateWorld.main(commandArgs);
        }

        {
            String[] commandArgs = { "-i", "/tmp/w.viv", "-t", "10", "-o", "/tmp/w2.viv" };
            RunSimulation.main(commandArgs);
        }
        {
            String[] commandArgs = { "-i", "/tmp/w2.viv", "-t", "10", "-o", "/tmp/w3.viv" };
            RunSimulation.main(commandArgs);
        }
        {
            String[] commandArgs = { "-i", "/tmp/w3.viv", "-t", "10", "-o", "/tmp/w4.viv" };
            RunSimulation.main(commandArgs);
        }
    }

    public static void inactive()
    {
        {
            String[] commandArgs = { "data/world_saves/medium.viv", "-s ", "25", "creatureMemoryUnitCount", "1" };
            CreateWorld.main(commandArgs);

        }

        {
            String[] commandArgs = { "data/world_saves/medium.viv", "20000", "data/world_saves/medium2.viv" };
            RunSimulation.main(commandArgs);
        }

        {
            String[] commandArgs = { "data/world_saves/medium.viv", "20000000", "data/world_saves/medium2.viv" };
            RunSimulation.main(commandArgs);
        }

        for (int i = 3; i < 10; i++)
        {
            System.out.println("Loading " + i + " and saving to " + (i + 1));
            String[] commandArgs = { "data/world_saves/medium" + i + ".viv", "200000",
                    "data/world_saves/medium" + (i + 1) + ".viv" };
            RunSimulation.main(commandArgs);
        }
        {
            String[] commandArgs = { "data/world_saves/world_viewer/tick0.viv" };
            RenderWorld.main(commandArgs);
        }

    }
}
