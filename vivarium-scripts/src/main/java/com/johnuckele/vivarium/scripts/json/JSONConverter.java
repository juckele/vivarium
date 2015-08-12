package com.johnuckele.vivarium.scripts.json;

import java.util.HashMap;

import org.json.JSONObject;

import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializedCollection;

public class JSONConverter
{
    public static JSONObject convertFromSerializedCollection(SerializedCollection collection)
    {
        JSONObject jsonObject = new JSONObject();
        for (SerializationCategory category : SerializationCategory.values())
        {
            while (collection.hasNext(category))
            {
                HashMap<String, Object> map = collection.popNext(category);
                JSONObject categoryMapObject = new JSONObject(map);
                jsonObject.append(category.name(), categoryMapObject);
            }
        }
        return jsonObject;
    }

    public static SerializedCollection convertFromJSONObject(JSONObject jsonObject)
    {
        SerializedCollection collection = new SerializedCollection();
        return collection;
    }
}