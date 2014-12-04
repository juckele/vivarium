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
		World w = null;
		File inputFile = new File(args[0]);
		try
		{
			FileInputStream fis = new FileInputStream(inputFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			w = (World) ois.readObject();
			ois.close();
			fis.close();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
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
			try
			{
				File outputFile = new File(args[2]);
				FileOutputStream fos;
				fos = new FileOutputStream(outputFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(w);
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
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
