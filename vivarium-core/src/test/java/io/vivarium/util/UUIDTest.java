package io.vivarium.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.test.FastTest;
import io.vivarium.test.UnitTest;

public class UUIDTest
{
    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testPrintAndParse()
    {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.fromString(uuid1.toString());
        Tester.isNotNull("UUID should be non-null", uuid1);
        Tester.isNotNull("UUID should be non-null", uuid2);
        Tester.equal("UUIDs should be the same", uuid1.toString(), uuid2.toString());
    }

    @Test
    @Category({ FastTest.class, UnitTest.class })
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
