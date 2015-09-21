package com.johnuckele.vivarium.scripts.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;

public class JSONConverter
{
    public static String serializerToJSONString(MapSerializer serializer)
    {
        SerializationEngine engine = new SerializationEngine();
        SerializedCollection collection = engine.serialize(serializer);
        JSONObject jsonObject = JSONConverter.convertFromSerializedCollection(collection);
        return jsonObject.toString();
    }

    public static MapSerializer jsonStringToSerializer(String jsonString)
    {
        JSONObject jsonObject = new JSONObject(jsonString);
        SerializedCollection collection = JSONConverter.convertFromJSONObject(jsonObject);
        SerializationEngine engine = new SerializationEngine();
        return engine.deserialize(collection);
    }

    public static List<MapSerializer> jsonStringToSerializerList(String jsonString)
    {
        JSONObject jsonObject = new JSONObject(jsonString);
        SerializedCollection collection = JSONConverter.convertFromJSONObject(jsonObject);
        SerializationEngine engine = new SerializationEngine();
        return engine.deserializeList(collection);
    }

    private static JSONObject convertFromSerializedCollection(SerializedCollection collection)
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

    private static SerializedCollection convertFromJSONObject(JSONObject jsonObject)
    {
        SerializedCollection collection = new SerializedCollection();
        for (Object key : jsonObject.keySet())
        {
            String category = "" + key;
            JSONArray categoryArray = jsonObject.getJSONArray(category);
            for (int i = 0; i < categoryArray.length(); i++)
            {
                JSONObject objectI = categoryArray.getJSONObject(i);
                HashMap<String, Object> map = innerConvertFromJSONObject(objectI);
                collection.addObject(map);
            }
        }
        return collection;
    }

    private static HashMap<String, Object> innerConvertFromJSONObject(JSONObject jsonObject)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Object key : jsonObject.keySet())
        {
            String keyString = key.toString();
            Object value = jsonObject.get(keyString);
            map.put(keyString, innerConvertFromJSON(value));
        }
        return map;

    }

    private static List<Object> innerConvertFromJSONArray(JSONArray jsonArray)
    {
        LinkedList<Object> list = new LinkedList<Object>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            Object value = jsonArray.get(i);
            list.add(innerConvertFromJSON(value));
        }
        return list;
    }

    private static Object innerConvertFromJSON(Object value)
    {
        if (value instanceof JSONArray)
        {
            return innerConvertFromJSONArray((JSONArray) value);
        }
        else if (value instanceof JSONObject)
        {
            return innerConvertFromJSONObject((JSONObject) value);
        }
        else
        {
            System.out.println("Value " + value + " has type " + (value != null ? value.getClass() : null));
            return value;
        }
    }
}