package com.johnuckele.vivarium.scripts.harness;

import org.junit.Test;

import com.johnuckele.vivarium.scripts.ConvertToJSON;
import com.johnuckele.vivarium.scripts.CreateAuditFunction;
import com.johnuckele.vivarium.scripts.CreateBlueprint;
import com.johnuckele.vivarium.scripts.CreateSpecies;
import com.johnuckele.vivarium.scripts.CreateWorld;
import com.johnuckele.vivarium.scripts.RenderWorld;
import com.johnuckele.vivarium.scripts.RunSimulation;

public class Harness
{
    @Test
    public void testWorldBuildWorkflow()
    {
        {
            String[] commandArgs = { "-o", "/tmp/af.viv", "auditType", "CENSUS" };
            CreateAuditFunction.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/s.viv", "maximumFood", "3000" };
            CreateSpecies.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/bp.viv", "-a", "/tmp/af.viv" };
            CreateBlueprint.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/bp.viv", "-a", "/tmp/af.viv", "-s", "/tmp/s.viv" };
            CreateBlueprint.main(commandArgs);
        }

        {
            String[] commandArgs = { "-o", "/tmp/w.viv", "-b", "/tmp/bp.viv" };
            CreateWorld.main(commandArgs);
        }

        {
            String[] commandArgs = { "/tmp/w.viv", "10", "/tmp/w2.viv" };
            RunSimulation.main(commandArgs);
        }
        {
            String[] commandArgs = { "/tmp/w2.viv", "10", "/tmp/w3.viv" };
            RunSimulation.main(commandArgs);
        }
        {
            String[] commandArgs = { "/tmp/w3.viv", "10", "/tmp/w4.viv" };
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
            String[] commandArgs = { "data/world_saves/medium2.viv", "data/world_saves/medium.json" };
            ConvertToJSON.main(commandArgs);
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
            String[] commandArgs = { "data/world_saves/world_viewer/tick0.viv", "data/world_saves/snap1.json" };
            ConvertToJSON.main(commandArgs);
        }
        {
            String[] commandArgs = { "data/world_saves/world_viewer/tick0.viv" };
            RenderWorld.main(commandArgs);
        }

    }
}
