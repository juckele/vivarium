package com.johnuckele.vivarium.scripts;

import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.visualization.RenderCode;

public class RenderUckeleoid extends Script
{
	public RenderUckeleoid(String[] args)
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
		Uckeleoid u = ScriptIO.loadUckeleoid(args[0], Format.JAVA_SERIALIZABLE);
		System.out.println(u.toString(RenderCode.BRAIN_WEIGHTS));
		System.out.println(u.toString(RenderCode.COMPLEX_UCKELEOID));

	}

	public static void main(String[] args)
	{
		new RenderUckeleoid(args);
	}
}
