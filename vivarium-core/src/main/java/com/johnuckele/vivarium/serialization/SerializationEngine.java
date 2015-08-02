package com.johnuckele.vivarium.serialization;

import java.util.HashMap;
import java.util.Map;

import com.johnuckele.vivarium.core.Species;

public class SerializationEngine
{
    public static final String                          ID_KEY                = "+id";
    public static final String                          CATEGORY_KEY          = "+category";
    public static final String                          CLASS_KEY             = "+class";
    public static final HashMap<String, String>         EMPTY_OBJECT_MAP      = new HashMap<String, String>();
    public static final HashMap<MapSerializer, Integer> EMPTY_REFERENCE_MAP   = new HashMap<MapSerializer, Integer>();
    public static final HashMap<Integer, MapSerializer> EMPTY_DEREFERENCE_MAP = new HashMap<Integer, MapSerializer>();

    private HashMap<MapSerializer, Integer> _referenceMap;
    private HashMap<Integer, MapSerializer> _dereferenceMap;

    public SerializationEngine()
    {
        _referenceMap = new HashMap<MapSerializer, Integer>();
        _dereferenceMap = new HashMap<Integer, MapSerializer>();
    }

    public MapSerializer deserialize(HashMap<String, String> map)
    {
        String type = map.get("+type");
        if (type.equals(Species.class.getSimpleName()))
        {
            Species s = Species.makeUninitialized();
            deserialize(s, map);
            s.finalizeDeserialization(map, EMPTY_DEREFERENCE_MAP);
            return s;
        }
        return null;
    }

    public SerializedCollection serialize(MapSerializer object)
    {
        SerializedCollection collection = new SerializedCollection();
        // Start by recursively serializing the top level object
        serializeObjectIntoCollection(object, collection);
        return collection;
    }

    private void serializeObjectIntoCollection(MapSerializer object, SerializedCollection collection)
    {
        if (!_referenceMap.containsKey(object))
        {
            // Serialize all references first
            for (MapSerializer reference : object.getReferences())
            {
                serializeObjectIntoCollection(reference, collection);
            }
            // Serialize the current object
            int objectID = collection.categoryCount(object.getSerializationCategory());
            System.out.println("ID " + objectID);
            _referenceMap.put(object, objectID);
            HashMap<String, String> map = serializeObject(object, objectID);
            collection.addObject(map);
        }
    }

    private HashMap<String, String> serializeObject(MapSerializer object, int id)
    {
        System.out.println("Serializing... " + object);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(ID_KEY, "" + id);
        map.put(CATEGORY_KEY, "" + object.getSerializationCategory());
        map.put(CLASS_KEY, "" + object.getClass().getSimpleName());
        try
        {
            for (SerializedParameter parameter : object.getMappedParameters())
            {
                map.put(parameter.getName(), object.getValue(parameter.getName()));
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        object.finalizeSerialization(map, EMPTY_REFERENCE_MAP);
        return map;
    }

    public static String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }

    public static void deserialize(MapSerializer object, Map<String, String> map)
    {
        System.out.println("Deserializing an object " + object);
        try
        {
            for (SerializedParameter parameter : object.getMappedParameters())
            {
                if (map.containsKey(parameter.getName()))
                {
                    object.setValue(parameter.getName(), map.get(parameter.getName()));
                }
                else
                {
                    object.setValue(parameter.getName(), parameter.getDefaultValue());
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        object.finalizeDeserialization(map, EMPTY_DEREFERENCE_MAP);
    }
}
