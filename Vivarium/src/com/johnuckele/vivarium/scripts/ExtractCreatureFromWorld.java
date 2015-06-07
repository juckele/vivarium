package com.johnuckele.vivarium.scripts;

import java.util.LinkedList;
import java.util.Random;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.World;

public class ExtractCreatureFromWorld extends Script
{
	public ExtractCreatureFromWorld(String[] args)
	{
		super(args);
	}

	@Override protected boolean argumentCountIsValid(int argCount)
	{
		if(argCount == 2 || argCount == 3)
		{
			return true;
		}
		return false;
	}

	@Override protected String getUsage()
	{
		return "Usage: java scriptPath worldInputFilePath creatureOutputFilePath [creatureID]";
	}

	@Override protected void run(String[] args)
	{
		// Load the world file
		World w = ScriptIO.loadWorld(args[0], Format.JAVA_SERIALIZABLE);

		// Find the specific Creature requested if one was requested
		Creature u = null;
		LinkedList<Creature> creatures = w.getAllCreatures();
		if(args.length == 3)
		{
			int creatureID = Integer.parseInt(args[2]);
			for(Creature tempU : creatures)
			{
				if(tempU.getID() == creatureID)
				{
					u = tempU;
				}
			}
			if ( u == null )
			{
				System.err.println("Uckeleloid with ID "+creatureID+" not found");
				System.exit(-1);
			}
		}
		// Otherwise find a random member
		else
		{
			Random random = new Random();
			u = creatures.get(random.nextInt(creatures.size()));
		}

		// Disconnect the Creature and save it
		u.disconnectFromWorld();
		ScriptIO.saveUckeleloid(u, args[1], Format.JAVA_SERIALIZABLE);
	}

	public static void main(String[] args)
	{
		new ExtractCreatureFromWorld(args);
	}
	
}
