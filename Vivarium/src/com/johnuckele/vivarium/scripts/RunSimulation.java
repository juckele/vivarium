package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;

public class RunSimulation extends Script
{
	public RunSimulation(String[] args)
	{
		super(args);
	}

	@Override protected boolean argumentCountIsValid(int argCount)
	{
		if(argCount == 2)
		{
			return true;
		}
		else if(argCount == 3)
		{
			return true;
		}
		return false;
	}

	@Override protected String getUsage()
	{
		return "Usage: java scriptPath inputFilePath ticks [outputFilePath]";
	}

	@Override protected void run(String[] args)
	{
		// Load
		World w = ScriptIO.loadWorld(args[0], Format.JAVA_SERIALIZABLE);
		int uckeleoidCount = w.getCount(WorldObject.UCKELEOID);
		System.out.println("Uckeleoid count in loaded world: "+uckeleoidCount);

		// Run simulation
		int simulationTicks = Integer.parseInt(args[1]);
		for(int i = 0; i < simulationTicks; i++)
		{
			w.tick();
		}
		uckeleoidCount = w.getCount(WorldObject.UCKELEOID);
		System.out.println("Uckeleoid count after simulations: "+uckeleoidCount);

		// Save
		if(args.length == 3)
		{
			System.out.println("Saving file: "+args[2]);
			ScriptIO.saveWorld(w, args[2], Format.JAVA_SERIALIZABLE);
		}
		else
		{
			System.out.println("Results not saved");
		}
	}

	public static void main(String[] args)
	{
		new RunSimulation(args);
	}
}
