package io.vivarium.serialization;

import java.util.Arrays;

public enum Format
{
    JSON, GWT;

    public static Format parseFormat(String format)
    {
        try
        {
            Format result = Format.valueOf(format);
            return result;
        }
        catch (Exception e)
        {
            System.out.println(
                    "Unable to decode format " + format + ". Legal values are " + Arrays.toString(Format.values()));
            throw new IllegalArgumentException();
        }
    }
}
