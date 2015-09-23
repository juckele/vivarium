package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

import org.json.JSONException;

import com.googlecode.gwtstreamer.client.Streamer;
import com.johnuckele.vivarium.scripts.json.JSONConverter;
import com.johnuckele.vivarium.serialization.MapSerializer;

public class ScriptIO
{
    public static void saveSerializer(MapSerializer serializer, String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            saveObjectWithJSON(serializer, fileName);
        }
        else if (f == Format.GWT)
        {
            saveObjectWithGwtStreamer(serializer, fileName);
        }
        else
        {
            throw new Error("Loading format " + f + " is not supported");
        }
    }

    public static void saveStringToFile(String dataString, String fileName)
    {
        FileOutputStream fos = null;
        try
        {
            File file = new File(fileName);
            fos = new FileOutputStream(file);
            byte[] jsonByteData = dataString.getBytes("UTF-8");
            fos.write(jsonByteData);
            fos.flush();
        }
        catch (IOException e)
        {
            System.out.print("Unable to write the file " + fileName + "\n");
            e.printStackTrace();
            System.exit(1);
        }
        finally
        {
            try
            {
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String loadFileToString(String fileName)
    {
        try
        {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file, "UTF-8");
            String dataString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            return dataString;
        }
        catch (FileNotFoundException e)
        {
            System.out.print("Unable to read the file " + fileName + "\n");
            e.printStackTrace();
            System.exit(1);
            return null; // Unreachable, but Java doesn't know this.
        }
    }

    private static void saveObjectWithGwtStreamer(MapSerializer serializer, String fileName)
    {
        try
        {
            String gwtString = Streamer.get().toString(serializer);
            saveStringToFile(gwtString, fileName);
        }
        catch (JSONException e)
        {
            System.out.print("Unable to write the create GWT Stream\n");
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void saveObjectWithJSON(MapSerializer serializer, String fileName)
    {
        try
        {
            String jsonString = JSONConverter.serializerToJSONString(serializer);
            saveStringToFile(jsonString, fileName);
        }
        catch (JSONException e)
        {
            System.out.print("Unable to write the create JSON\n");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static MapSerializer loadObject(String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            return loadObjectWithJSON(fileName);
        }
        else if (f == Format.GWT)
        {
            return loadObjectWithGwtStreamer(fileName);
        }
        else
        {
            throw new Error("Loading format " + f + " is not supported");
        }
    }

    public static Collection<MapSerializer> loadObjects(String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            return loadObjectsWithJSON(fileName);
        }
        else
        {
            throw new Error("Loading format " + f + " is not supported");
        }
    }

    private static MapSerializer loadObjectWithJSON(String fileName)
    {
        String jsonString = loadFileToString(fileName);
        return JSONConverter.jsonStringToSerializer(jsonString);
    }

    private static Collection<MapSerializer> loadObjectsWithJSON(String fileName)
    {
        String jsonString = loadFileToString(fileName);
        return JSONConverter.jsonStringToSerializerList(jsonString);
    }

    private static MapSerializer loadObjectWithGwtStreamer(String fileName)
    {
        String gwtString = loadFileToString(fileName);
        return (MapSerializer) Streamer.get().fromString(gwtString);
    }
}
