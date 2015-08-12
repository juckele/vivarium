package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.johnuckele.vivarium.scripts.json.JSONConverter;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;

public class ScriptIO
{
    public static void saveSerializer(MapSerializer serializer, String fileName, Format f)
    {
        if (f == Format.JAVA_SERIALIZABLE)
        {
            saveObjectWithDefaultSerialization(serializer, fileName);
        }
        else if (f == Format.JSON)
        {
            saveObjectWithJSON(serializer, fileName);
        }
        else
        {
            throw new Error("Loading format " + f + " is not supported");
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
        catch (IOException e)
        {
            System.out.print("Unable to write the file " + fileName + "\n");
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
        catch (IOException e)
        {
            System.out.print("Unable to write the file " + fileName + "\n");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void loadStringToFile(String dataString, String fileName)
    {
        // TODO WRITE
        throw new UnsupportedOperationException("Write this");
    }

    private static void saveObjectWithJSON(MapSerializer serializer, String fileName)
    {
        try
        {
            SerializationEngine engine = new SerializationEngine();
            SerializedCollection collection = engine.serialize(serializer);
            System.out.println(collection);
            JSONObject jsonObject = JSONConverter.convertFromSerializedCollection(collection);
            System.out.println(jsonObject.toString());
            saveStringToFile(jsonObject.toString(), fileName);
        }
        catch (JSONException e)
        {
            System.out.print("Unable to write the create JSON\n");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static Object loadObject(String fileName, Format f)
    {
        if (f == Format.JAVA_SERIALIZABLE)
        {
            return loadObjectWithDefaultSerialization(fileName);
        }
        else if (f == Format.JSON)
        {
            return loadObjectWithJSON(fileName);
        }
        else
        {
            throw new Error("Loading format " + f + " is not supported");
        }
    }

    private static Object loadObjectWithDefaultSerialization(String fileName)
    {
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
        catch (ClassNotFoundException e)
        {
            System.out.print("Unrecognized class on load attempt\n");
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.print("Unable to read the file " + fileName + "\n");
            e.printStackTrace();
            System.exit(1);
        }
        return (o);
    }

    private static Object loadObjectWithJSON(String fileName)
    {
        // TODO WRITE
        throw new UnsupportedOperationException("Write this");
    }
}
