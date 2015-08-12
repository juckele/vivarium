package com.johnuckele.vivarium.scripts.json;

import java.util.HashMap;

import org.json.JSONArray;
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
            if (collection.categoryCount(category) > 0)
            {
                JSONArray categoryArray = new JSONArray();
                jsonObject.append(category.name(), categoryArray);
                while (collection.hasNext(category))
                {
                    HashMap<String, String> map = collection.popNext(category);
                    JSONObject mapObject = new JSONObject(map);
                    categoryArray.put(mapObject);
                }
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