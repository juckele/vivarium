package io.vivarium.scripts;

import io.vivarium.core.World;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.visualization.RenderCode;

public class RenderWorld extends Script
{
    public RenderWorld(String[] args)
    {
        super(args);
    }

    @Override
    protected boolean argumentCountIsValid(int argCount)
    {
        if (argCount == 1)
        {
            return true;
        }
        return false;
    }

    @Override
    protected String getUsage()
    {
        return "Usage: java scriptPath filePath";
    }

    @Override
    protected void run(String[] args)
    {
        World w = FileIO.loadObjectCollection(args[0], Format.JSON).getFirst(World.class);
        System.out.println(w.render(RenderCode.WORLD_MAP));
        System.out.println(w.render(RenderCode.BRAIN_WEIGHTS));
    }

    public static void main(String[] args)
    {
        new RenderWorld(args);
    }
}
