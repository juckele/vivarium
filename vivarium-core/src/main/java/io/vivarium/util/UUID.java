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

import java.io.Serializable;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;
import com.googlecode.gwtstreamer.client.Streamable;

/**
 * Guava based UUID implementation for compatibility with GWT. This class can be used as a plug in replacement for the
 * standard java.util.UUID, but it's worth noting that GWT does not support SecureRandom, so this UUID implementation
 * does not provide the same guarantee of entropy. It's much preferable to generate UUIDs from the java.util.UUID and
 * convert into this class before sending to GWT if possible.
 *
 * @author John H. Uckele
 */
public class UUID implements Serializable, Streamable
{
    private static final long serialVersionUID = -3965221404087809719L;
    private static final BaseEncoding ENCODING = BaseEncoding.base16().lowerCase();
    private final long _long1;
    private final long _long2;

    private UUID()
    {
        _long1 = 0;
        _long2 = 0;
    }

    private UUID(long long1, long long2)
    {
        _long1 = long1;
        _long2 = long2;
    }

    public static UUID randomUUID()
    {
        // This is compatible with a real UUID, but it is not cryptographically secure.
        return new UUID(Rand.getInstance().getRandomLong(), Rand.getInstance().getRandomLong2());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // encode first long
        byte[] bytes1 = Longs.toByteArray(_long1);
        sb.append(ENCODING.encode(bytes1, 0, 4)).append('-');
        sb.append(ENCODING.encode(bytes1, 4, 2)).append('-');
        sb.append(ENCODING.encode(bytes1, 6, 2)).append('-');
        // encode second long
        byte[] bytes2 = Longs.toByteArray(_long2);
        sb.append(ENCODING.encode(bytes2, 0, 2)).append('-');
        sb.append(ENCODING.encode(bytes2, 2, 6));

        return sb.toString();
    }

    public static UUID fromString(String s)
    {
        s = s.replaceAll("-", "").toLowerCase();

        // decode first long
        byte[] bytes1 = ENCODING.decode(s.substring(0, 16));
        long long1 = Longs.fromByteArray(bytes1);

        // decode second long
        byte[] bytes2 = ENCODING.decode(s.substring(16, 32));
        long long2 = Longs.fromByteArray(bytes2);

        return new UUID(long1, long2);
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object that)
    {
        return this.toString().equals(String.valueOf(that));
    }
}