package io.vivarium.serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.util.UserFacingError;

public class FileIO
{
    public static final int FILE_FORMAT_VERSION = 1;

    public static void saveSerializer(VivariumObject serializer, String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            saveObjectWithJSON(serializer, fileName);
        }
        else
        {
            throw new UserFacingError("Writing format " + f + " is not supported.");
        }
    }

    public static void saveSerializerCollection(VivariumObjectCollection serializer, String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            saveObjectCollectionWithJSON(serializer, fileName);
        }
        else if (f == Format.GWT)
        {
            saveObjectCollectionWithGwtStreamer(serializer, fileName);
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

    private static void saveObjectCollectionWithGwtStreamer(VivariumObjectCollection serializer, String fileName)
    {
        String gwtString = Streamer.get().toString(serializer);
        saveStringToFile(gwtString, fileName);
    }

    private static void saveObjectWithJSON(VivariumObject serializer, String fileName)
    {
        String jsonString = JSONConverter.serializerToJSONString(serializer);
        saveStringToFile(jsonString, fileName);
    }

    private static void saveObjectCollectionWithJSON(VivariumObjectCollection serializer, String fileName)
    {
        String jsonString = JSONConverter.serializerToJSONString(serializer);
        saveStringToFile(jsonString, fileName);
    }

    public static VivariumObjectCollection loadObjectCollection(String fileName, Format f)
    {
        if (f == Format.JSON)
        {
            return loadObjectCollectionWithJSON(fileName);
        }
        else if (f == Format.GWT)
        {
            return loadObjectCollectionWithGwtStreamer(fileName);
        }
        else
        {
            throw new UserFacingError("Loading format " + f + " is not supported");
        }
    }

    private static VivariumObjectCollection loadObjectCollectionWithJSON(String fileName)
    {
        String jsonString = loadFileToString(fileName);
        return JSONConverter.jsonStringToSerializerCollection(jsonString);
    }

    // TODO: This is no longer connected and needs to be reconnected somehow.
    private static VivariumObjectCollection loadObjectCollectionWithGwtStreamer(String fileName)
    {
        String gwtString = loadFileToString(fileName);
        return (VivariumObjectCollection) Streamer.get().fromString(gwtString);
    }
}
