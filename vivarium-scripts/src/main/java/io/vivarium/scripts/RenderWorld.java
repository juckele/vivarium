package io.vivarium.scripts;

import io.vivarium.core.RenderCode;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;

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
        GridWorld w = FileIO.loadObjectCollection(args[0], Format.JSON).getFirst(GridWorld.class);
        System.out.println(w.render(RenderCode.WORLD_MAP));
    }

    public static void main(String[] args)
    {
        new RenderWorld(args);
    }
}
