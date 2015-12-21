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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class UUIDTest
{
    @Test
    public void testPrintAndParse()
    {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.fromString(uuid1.toString());
        Tester.isNotNull("UUID should be non-null", uuid1);
        Tester.isNotNull("UUID should be non-null", uuid2);
        Tester.equal("UUIDs should be the same", uuid1.toString(), uuid2.toString());
    }

    @Test
    public void testReservedBits()
    {
        UUID uuid = UUID.randomUUID();
        Tester.isNotNull("UUID should be non-null", uuid);
        Tester.equal("13th nibble is 4", uuid.toString().charAt(14), '4');
        Set<Character> validCharacters = new HashSet<>();
        validCharacters.add('8');
        validCharacters.add('9');
        validCharacters.add('a');
        validCharacters.add('b');
        Tester.contains("17th nibble is 8, 9, a, or b ", validCharacters, uuid.toString().charAt(19));
    }

}
