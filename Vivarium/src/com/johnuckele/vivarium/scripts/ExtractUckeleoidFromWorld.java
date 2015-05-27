package com.johnuckele.vivarium.scripts;

import java.util.LinkedList;
import java.util.Random;

import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.core.World;

public class ExtractUckeleoidFromWorld extends Script
{
	public ExtractUckeleoidFromWorld(String[] args)
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
		return "Usage: java scriptPath worldInputFilePath uckeleoidOutputFilePath [uckeleoidID]";
	}

	@Override protected void run(String[] args)
	{
		// Load the world file
		World w = ScriptIO.loadWorld(args[0], Format.JAVA_SERIALIZABLE);

		// Find the specific Uckeleoid requested if one was requested
		Uckeleoid u = null;
		LinkedList<Uckeleoid> uckeleoids = w.getAllUckeleoids();
		if(args.length == 3)
		{
			int uckeleoidId = Integer.parseInt(args[2]);
			for(Uckeleoid tempU : uckeleoids)
			{
				if(tempU.getID() == uckeleoidId)
				{
					u = tempU;
				}
			}
			if ( u == null )
			{
				System.err.println("Uckeleloid with ID "+uckeleoidId+" not found");
				System.exit(-1);
			}
		}
		// Otherwise find a random member
		else
		{
			Random random = new Random();
			u = uckeleoids.get(random.nextInt(uckeleoids.size()));
		}

		// Disconnect the Uckeleoid and save it
		u.disconnectFromWorld();
		ScriptIO.saveUckeleloid(u, args[1], Format.JAVA_SERIALIZABLE);
	}

	public static void main(String[] args)
	{
		new ExtractUckeleoidFromWorld(args);
	}
	
}
