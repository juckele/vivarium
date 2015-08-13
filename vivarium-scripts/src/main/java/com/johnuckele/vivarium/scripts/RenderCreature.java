package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.visualization.RenderCode;

public class RenderCreature extends Script
{
    public RenderCreature(String[] args)
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
        Creature c = (Creature) ScriptIO.loadObject(args[0], Format.JSON);
        System.out.println(c.getBrain().render(RenderCode.BRAIN_WEIGHTS));
        System.out.println(c.render(RenderCode.COMPLEX_CREATURE, -1, -1));

    }

    public static void main(String[] args)
    {
        new RenderCreature(args);
    }
}
