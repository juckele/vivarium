package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.johnuckele.vivarium.World;
import com.johnuckele.vivarium.WorldObject;

public abstract class Script
{
	public Script(String[] args)
	{
		if(argumentCountIsValid(args.length))
		{
			run(args);
		}
		else
		{
			printUsageAndExit();
		}
	}
	
	protected abstract boolean argumentCountIsValid(int argCount);
	protected abstract String getUsage();
	protected abstract void run(String[] args);

	protected void printUsageAndExit()
	{
		System.out.println(getUsage());
		System.exit(0);
	}

	protected void saveWorld(World w, String fileName)
	{
		File f = new File(fileName);
		try
		{
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(w);
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
		}
		catch(IOException e)
		{
			System.out.print("Unable to write the file "+fileName+"\n");
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected World loadWorld(String fileName)
	{
		World w = null;
		File inputFile = new File(fileName);
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
			System.out.print("Unrecognized class on load attempt\n");
			e.printStackTrace();
			System.exit(1);
		}
		catch(IOException e)
		{
			System.out.print("Unable to read the file "+fileName+"\n");
			e.printStackTrace();
			System.exit(1);
		}
		return(w);
	}
}
