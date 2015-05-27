package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.core.World;

public class ScriptIO
{
	public static void saveUckeleloid(Uckeleoid u, String fileName, Format f)
	{
		if(f == Format.JAVA_SERIALIZABLE)
		{
			saveObjectWithDefaultSerialization(u, fileName);
		}
		else
		{
			throw new Error("Loading format "+f+" is not supported");
		}
	}

	public static void saveWorld(World w, String fileName, Format f)
	{
		if(f == Format.JAVA_SERIALIZABLE)
		{
			saveObjectWithDefaultSerialization(w, fileName);
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

	private static void saveObjectWithDefaultSerialization(Object o, String fileName)
	{
		File file = new File(fileName);
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
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

	public static void saveStringToFile(String dataString, String fileName)
	{
		try
		{
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] jsonByteData = dataString.getBytes();
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
	}
	private static void saveWorldWithJSON(World w, String fileName)
	{
		try
		{
			JSONObject jsonObject = JSONEncoder.convertWorldToJSON(w);
			System.out.println(jsonObject.toString());
			saveStringToFile(jsonObject.toString(), fileName);
		}
		catch(JSONException e)
		{
			System.out.print("Unable to write the create JSON\n");
			e.printStackTrace();
			System.exit(2);
		}
	}

	public static World loadWorld(String fileName, Format f)
	{
		World w = (World) loadObject(fileName, f);
		return w;

	}

	public static Uckeleoid loadUckeleoid(String fileName, Format f)
	{
		Uckeleoid u = (Uckeleoid) loadObject(fileName, f);
		return u;
	}

	private static Object loadObject(String fileName, Format f)
	{
		if(f != Format.JAVA_SERIALIZABLE)
		{
			throw new Error("Loading format "+f+" is not supported");
		}
		Object o = null;
		File inputFile = new File(fileName);
		try
		{
			FileInputStream fis = new FileInputStream(inputFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			o = ois.readObject();
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
		return(o);
	}
}
