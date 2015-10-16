/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 John H. Uckele
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
 * Guava based UUID implementation for compatibility with GWT. This class is more or less a plug in replacement for the
 * standard java.util.UUID.
 *
 * @author John H. Uckele
 */
public class UUID implements Serializable, Streamable
{
    private static final long serialVersionUID = -3581776520796287694L;
    private long _long1;
    private long _long2;

    private UUID()
    {
    }

    public static UUID randomUUID()
    {
        UUID uuid = new UUID();

        uuid._long1 = Rand.getRandomLong();
        uuid._long2 = Rand.getRandomLong();

        return uuid;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // encode first long
        byte[] bytes1 = Longs.toByteArray(_long1);
        sb.append(BaseEncoding.base16().encode(bytes1, 0, 4)).append('-');
        sb.append(BaseEncoding.base16().encode(bytes1, 4, 2)).append('-');
        sb.append(BaseEncoding.base16().encode(bytes1, 6, 2)).append('-');
        // encode second long
        byte[] bytes2 = Longs.toByteArray(_long2);
        sb.append(BaseEncoding.base16().encode(bytes2, 0, 2)).append('-');
        sb.append(BaseEncoding.base16().encode(bytes2, 2, 6));

        return sb.toString();
    }

    public static UUID fromString(String s)
    {
        UUID vid = new UUID();

        s = s.replaceAll("-", "");

        // decode first long
        byte[] bytes1 = BaseEncoding.base16().decode(s.substring(0, 16));
        vid._long1 = Longs.fromByteArray(bytes1);

        // decode second long
        byte[] bytes2 = BaseEncoding.base16().decode(s.substring(16, 32));
        vid._long2 = Longs.fromByteArray(bytes2);

        return vid;
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