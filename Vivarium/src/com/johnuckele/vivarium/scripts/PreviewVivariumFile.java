package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.visualization.RenderCode;

public class PreviewVivariumFile extends Script
{
	public PreviewVivariumFile(String[] args)
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
		World w = loadWorld(args[0]);
		System.out.println(w.toString(RenderCode.MAP));
		System.out.println(w.toString(RenderCode.BRAIN_WEIGHTS));
		System.out.println(w.toString(RenderCode.UCKELEOID_LIST));
		System.out.println(w.toString(RenderCode.DEAD_UCKELEOID_LIST));
	}

	public static void main(String[] args)
	{
		new PreviewVivariumFile(args);
	}
}
