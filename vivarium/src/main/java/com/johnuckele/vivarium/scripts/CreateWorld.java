package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;

public class CreateWorld extends Script
{
    public CreateWorld(String[] args)
    {
        super(args);
    }

    @Override
    protected boolean argumentCountIsValid(int argCount)
    {
        // Two arguments are okay
        if (argCount == 2)
        {
            return true;
        }
        // More than two args are okay as long as the extras come in pairs
        else if (argCount > 2 && argCount % 2 == 0)
        {
            return true;
        }
        return false;
    }

    @Override
    protected String getUsage()
    {
        return "Usage: java scriptPath filePath dimensions [worldVariableKey worldVariableValue]*";
    }

    @Override
    protected void run(String[] args)
    {
        WorldVariables worldVariables = new WorldVariables(1);
        // For each pair of extra arguments after the first two, set a
        // worldVariable value
        for (int i = 2; i + 1 < args.length; i += 2)
        {
            worldVariables.setKeyValue(args[i], args[i + 1]);
        }

        World w = new World(Integer.parseInt(args[1]), worldVariables);

        int creatureCount = w.getCount(WorldObject.CREATURE);
        System.out.println("Creature count in new world: " + creatureCount);

        ScriptIO.saveWorld(w, args[0], Format.JAVA_SERIALIZABLE);
    }

    public static void main(String[] args)
    {
        new CreateWorld(args);
    }
}
