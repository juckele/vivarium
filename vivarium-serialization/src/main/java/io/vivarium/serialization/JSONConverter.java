package io.vivarium.serialization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONConverter
{
    private static final String OBJECT_KEY = "objects";
    private static final String VERSION_KEY = "fileFormatVersion";

    public static String serializerToJSONString(VivariumObject serializer)
    {
        SerializationEngine engine = new SerializationEngine();
        MapCollection collection = engine.serialize(serializer);
        JSONObject jsonObject = JSONConverter.convertFromSerializedCollection(collection);
        return jsonObject.toString();
    }

    public static String serializerToJSONString(VivariumObjectCollection serializers)
    {
        SerializationEngine engine = new SerializationEngine();
        MapCollection collection = engine.serialize(serializers);
        JSONObject jsonObject = JSONConverter.convertFromSerializedCollection(collection);
        return jsonObject.toString();
    }

    public static VivariumObjectCollection jsonStringToSerializerCollection(String jsonString)
    {
        JSONObject jsonObject = new JSONObject(jsonString);
        MapCollection collection = JSONConverter.convertFromJSONObject(jsonObject);
        SerializationEngine engine = new SerializationEngine();
        return engine.deserializeCollection(collection);
    }

    private static JSONObject convertFromSerializedCollection(MapCollection collection)
    {
        JSONObject jsonObject = new JSONObject();
        while (collection.hasNext())
        {
            HashMap<String, Object> map = collection.popNext();
            JSONObject categoryMapObject = new JSONObject(map);
            jsonObject.append(OBJECT_KEY, categoryMapObject);
        }
        jsonObject.put(VERSION_KEY, FileIO.FILE_FORMAT_VERSION);
        return jsonObject;
    }

    private static MapCollection convertFromJSONObject(JSONObject jsonObject)
    {
        MapCollection collection = new MapCollection();

        JSONArray categoryArray = jsonObject.getJSONArray(OBJECT_KEY);
        for (int i = 0; i < categoryArray.length(); i++)
        {
            JSONObject objectI = categoryArray.getJSONObject(i);
            HashMap<String, Object> map = innerConvertFromJSONObject(objectI);
            collection.addObject(map);
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
        else if (value.equals(JSONObject.NULL))
        {
            return null;
        }
        else
        {
            return value;
        }
    }
}