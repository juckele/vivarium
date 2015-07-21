package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.visualization.RenderCode;

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
        World w = ScriptIO.loadWorld(args[0], Format.JAVA_SERIALIZABLE);
        System.out.println(w.render(RenderCode.WORLD_MAP));
        System.out.println(w.render(RenderCode.BRAIN_WEIGHTS));
        // System.out.println(w.toString(RenderCode.CREATURE_LIST));
        // System.out.println(w.toString(RenderCode.DEAD_CREATURE_LIST));
    }

    public static void main(String[] args)
    {
        new RenderWorld(args);
    }
}