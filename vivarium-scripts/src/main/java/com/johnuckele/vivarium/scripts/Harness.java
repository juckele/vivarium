package com.johnuckele.vivarium.scripts;

public class Harness
{
    public static void main(String[] args)
    {
        System.out.println("Harness Testing");
        {
            String[] commandArgs = { "-o", "/tmp/af.viv", "auditType", "CENSUS" };
            CreateAuditFunction.main(commandArgs);
        }

        System.out.println("Harness Testing");
        {
            String[] commandArgs = { "-o", "/tmp/bp.viv", "-a", "/tmp/af.viv" };
            CreateBlueprint.main(commandArgs);
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
