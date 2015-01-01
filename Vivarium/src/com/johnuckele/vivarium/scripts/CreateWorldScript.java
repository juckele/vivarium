package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;

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

		saveWorld(w, args[0], Format.JAVA_SERIALIZABLE);
	}

	public static void main(String[] args)
	{
		new CreateWorldScript(args);
	}
}
