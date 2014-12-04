package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.johnuckele.vivarium.World;
import com.johnuckele.vivarium.WorldObject;

public class CreateWorldScript extends Script
{
	public CreateWorldScript(String[] args)
	{
		super(args);
	}

	@Override protected boolean argumentCountIsValid(int argCount)
	{
		if(argCount == 2)
		{
			return true;
		}
		return false;
	}

	@Override protected String getUsage()
	{
		return "Usage: java scriptPath filePath dimensions";
	}

	@Override protected void run(String[] args)
	{
		World w = new World(Integer.parseInt(args[1]));

		int uckeleoidCount = w.getCount(WorldObject.UCKELEOID);
		System.out.println("Uckeleoid count in new world: "+uckeleoidCount);

		saveWorld(w, args[0]);
	}

	public static void main(String[] args)
	{
		new CreateWorldScript(args);
	}
}
