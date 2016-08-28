package io.vivarium.scripts;

import io.vivarium.core.Creature;
import io.vivarium.core.RenderCode;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;

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
        Creature c = FileIO.loadObjectCollection(args[0], Format.JSON).getFirst(Creature.class);
        System.out.println(c.render(RenderCode.COMPLEX_CREATURE, -1, -1));

    }

    public static void main(String[] args)
    {
        new RenderCreature(args);
    }
}
