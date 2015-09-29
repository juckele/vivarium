package io.vivarium.scripts.json;

import org.junit.Test;

import io.vivarium.core.Blueprint;
import io.vivarium.core.World;
import com.johnuckele.vtest.Tester;

public class JSONConverterTest
{
    @Test
    public void testSaveLoadSaveWorld()
    {
        Blueprint blueprint = Blueprint.makeDefault();
        World worldOriginal = new World(blueprint);
        String jsonString1 = JSONConverter.serializerToJSONString(worldOriginal);
        World worldCopy = (World) JSONConverter.jsonStringToSerializer(jsonString1);
        String jsonString2 = JSONConverter.serializerToJSONString(worldCopy);
        System.out.println(jsonString1);
        System.out.println(jsonString2);
        Tester.notEqual("jsonStrings shot not be empty", jsonString1.length(), 0);
        Tester.equal("jsonStrings should also be the same length", jsonString1.length(), jsonString2.length());
    }

}
