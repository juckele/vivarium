package io.vivarium.scripts.json;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.World;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class JSONConverterTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testSaveLoadSaveWorld()
    {
        WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
        World worldOriginal = new World(worldBlueprint);
        String jsonString1 = JSONConverter.serializerToJSONString(worldOriginal);
        VivariumObjectCollection copiedCollection = JSONConverter.jsonStringToSerializerCollection(jsonString1);
        World worldCopy = copiedCollection.getFirst(World.class);
        String jsonString2 = JSONConverter.serializerToJSONString(worldCopy);
        System.out.println(jsonString1);
        System.out.println(jsonString2);
        Tester.notEqual("jsonStrings shot not be empty", jsonString1.length(), 0);
        Tester.equal("jsonStrings should also be the same length", jsonString1.length(), jsonString2.length());
    }

}
