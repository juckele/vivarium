package io.vivarium.serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.util.UserFacingError;

public class FileIO
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
            throw new UserFacingError("Writing format " + f + " is not supported.");
        }
    }

    public static void saveSerializerCollection(MapSerializerCollection serializer, String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            saveObjectCollectionWithJSON(serializer, fileName);
        }
        else
        {
            throw new UserFacingError("Writing format " + f + " is not supported.");
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
            throw new UserFacingError("Unable to write the file " + fileName);
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
                throw new UserFacingError("Unable to close the file stream");
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
            throw new UserFacingError("Unable to read the file " + fileName);
        }
    }

    private static void saveObjectWithGwtStreamer(MapSerializer serializer, String fileName)
    {
        String gwtString = Streamer.get().toString(serializer);
        saveStringToFile(gwtString, fileName);
    }

    private static void saveObjectWithJSON(MapSerializer serializer, String fileName)
    {
        String jsonString = JSONConverter.serializerToJSONString(serializer);
        saveStringToFile(jsonString, fileName);
    }

    private static void saveObjectCollectionWithJSON(MapSerializerCollection serializer, String fileName)
    {
        String jsonString = JSONConverter.serializerToJSONString(serializer);
        saveStringToFile(jsonString, fileName);
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
            throw new UserFacingError("Loading format " + f + " is not supported");
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
            throw new UserFacingError("Loading format " + f + " is not supported");
        }
    }

    public static MapSerializerCollection loadObjectCollection(String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            return loadObjectCollectionWithJSON(fileName);
        }
        else
        {
            throw new UserFacingError("Loading format " + f + " is not supported");
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

    private static MapSerializerCollection loadObjectCollectionWithJSON(String fileName)
    {
        String jsonString = loadFileToString(fileName);
        return JSONConverter.jsonStringToSerializerCollection(jsonString);
    }

    private static MapSerializer loadObjectWithGwtStreamer(String fileName)
    {
        String gwtString = loadFileToString(fileName);
        return (MapSerializer) Streamer.get().fromString(gwtString);
    }
}
