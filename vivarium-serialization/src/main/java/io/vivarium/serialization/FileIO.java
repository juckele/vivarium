/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

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
    public static void saveSerializer(VivariumObject serializer, String fileName, Format f)
    {
        String data = null;
        if (f == Format.JSON)
        {
            data = JSONConverter.serializerToJSONString(serializer);
        }
        else
        {
            throw new UserFacingError("Writing format " + f + " is not supported.");
        }
        saveStringToFile(data, fileName);
    }

    public static void saveSerializerCollection(VivariumObjectCollection serializer, String fileName, Format f)
    {
        String data = null;
        if (f == Format.JSON)
        {
            data = JSONConverter.serializerToJSONString(serializer);
        }
        else if (f == Format.GWT)
        {
            data = Streamer.get().toString(serializer);
        }
        else
        {
            throw new UserFacingError("Writing format " + f + " is not supported.");
        }
        saveStringToFile(data, fileName);
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

    public static VivariumObjectCollection loadObjectCollection(String fileName, Format f)
    {
        String data = loadFileToString(fileName);
        if (f == Format.JSON)
        {
            return JSONConverter.jsonStringToSerializerCollection(data);
        }
        else if (f == Format.GWT)
        {
            return (VivariumObjectCollection) Streamer.get().fromString(data);
        }
        else
        {
            throw new UserFacingError("Loading format " + f + " is not supported");
        }
    }
}
