package com.johnuckele.vivarium.scripts.json;

import org.junit.Test;

import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vtest.Tester;

public class JSONConverterTest
{
    @Test
    public void testSaveLoadSaveWorld()
    {
        Blueprint blueprint = Blueprint.makeDefault();
        World worldOriginal = new World(blueprint);
        String jsonString1 = JSONConverter.serializerToJSONString(worldOriginal);
        World worldCopy = (World) JSONConverter.jsonStringtoSerializer(jsonString1);
        String jsonString2 = JSONConverter.serializerToJSONString(worldCopy);
        Tester.notEqual("jsonStrings shot not be empty", jsonString1.length(), 0);
        Tester.equal("jsonStrings should also be the same length", jsonString1.length(), jsonString2.length());
    }

}
