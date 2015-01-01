package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.johnuckele.vivarium.core.World;

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

	public static void saveWorld(World w, String fileName, Format f)
	{
		if(f == Format.JAVA_SERIALIZABLE)
		{
			saveWorldWithDefaultSerialization(w, fileName);
		}
		else if(f == Format.JSON)
		{
			saveWorldWithJSON(w, fileName);
		}
		else
		{
			throw new Error("Loading format "+f+" is not supported");
		}
	}

	private static void saveWorldWithDefaultSerialization(World w, String fileName)
	{
		File file = new File(fileName);
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
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

	private static void saveWorldWithJSON(World w, String fileName)
	{
		JSONObject jsonObject;
		try
		{
			jsonObject = JSONEncoder.convertWorldToJSON(w);
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			System.out.println(jsonObject.toString());
			byte[] jsonByteData = jsonObject.toString().getBytes();
			fos.write(jsonByteData);
			fos.flush();
			fos.close();
		}
		catch(IOException e)
		{
			System.out.print("Unable to write the file "+fileName+"\n");
			e.printStackTrace();
			System.exit(1);
		}
		catch(JSONException e)
		{
			System.out.print("Unable to write the create JSON\n");
			e.printStackTrace();
			System.exit(2);
		}

	}

	protected World loadWorld(String fileName, Format f)
	{
		if(f != Format.JAVA_SERIALIZABLE)
		{
			throw new Error("Loading format "+f+" is not supported");
		}
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
