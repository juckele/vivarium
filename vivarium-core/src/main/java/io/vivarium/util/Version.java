/*
 * The MIT License (MIT)
 *
 * Copyright © 2015 John H. Uckele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright
 * notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.vivarium.util;

import com.google.common.base.Preconditions;

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
