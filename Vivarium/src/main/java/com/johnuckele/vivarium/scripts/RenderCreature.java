package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.visualization.RenderCode;

public class RenderCreature extends Script
{
	public RenderCreature(String[] args)
	{
		super(args);
	}

	@Override protected boolean argumentCountIsValid(int argCount)
	{
		if(argCount == 1)
		{
			return true;
		}
		return false;
	}

	@Override protected String getUsage()
	{
		return "Usage: java scriptPath filePath";
	}

	@Override protected void run(String[] args)
	{
		Creature u = ScriptIO.loadCreature(args[0], Format.JAVA_SERIALIZABLE);
		System.out.println(u.toString(RenderCode.BRAIN_WEIGHTS));
		System.out.println(u.toString(RenderCode.COMPLEX_CREATURE));

	}

	public static void main(String[] args)
	{
		new RenderCreature(args);
	}
}
