package io.vivarium.util;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Version
{
    private static final int CURRENT_MAJOR = 0;
    private static final int CURRENT_MINOR = 3;
    private static final int CURRENT_PATCH = 2;

    public static final int FILE_FORMAT_VERSION = 1;
    public static final int NETWORK_PROTOCOL_VERSION = 1;

    public static final Version CURRENT_VERSION = new Version();

    private final int major;
    private final int minor;
    private final int patch;

    private Version()
    {
        major = CURRENT_MAJOR;
        minor = CURRENT_MINOR;
        patch = CURRENT_PATCH;
    }

    public Version(int major, int minor, int patch)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public String toString()
    {
        return major + "." + minor + "." + patch;
    }

    public int[] toArray()
    {
        return new int[] { major, minor, patch };
    }

    public Version(String string)
    {
        String[] chunks = string.split("\\.");
        if (chunks.length == 3)
        {
            this.major = Integer.parseInt(chunks[0]);
            this.minor = Integer.parseInt(chunks[1]);
            this.patch = Integer.parseInt(chunks[2]);
        }
        else
        {
            throw new IllegalArgumentException("Unable to parse version not in format X.Y.Z: " + string);
        }
    }

    public Version(int[] versionNumbers)
    {
        Preconditions.checkArgument(versionNumbers.length == 3, "Input array for a version must be three elements");
        this.major = versionNumbers[0];
        this.minor = versionNumbers[1];
        this.patch = versionNumbers[2];
    }

}
