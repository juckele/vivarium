package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.johnuckele.vivarium.World;
import com.johnuckele.vivarium.WorldObject;

public class RunSimulationScript extends Script
{
	public RunSimulationScript(String[] args)
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
		World w = loadWorld(args[0]);
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
			saveWorld(w, args[2]);
		}
		else
		{
			System.out.println("Results not saved");
		}
	}

	public static void main(String[] args)
	{
		new RunSimulationScript(args);
	}
}
