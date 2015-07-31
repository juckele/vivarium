package com.johnuckele.vivarium.serialization;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.serialization.annotations.BooleanParameter;
import com.johnuckele.vivarium.serialization.annotations.BrainTypeParameter;
import com.johnuckele.vivarium.serialization.annotations.DoubleParameter;
import com.johnuckele.vivarium.serialization.annotations.IntegerParameter;

public class SerializationEngine
{
    public static final String                          ID_KEY                = "+id";
    public static final String                          CATEGORY_KEY          = "+category";
    public static final String                          CLASS_KEY             = "+class";
    public static final HashMap<String, String>         EMPTY_OBJECT_MAP      = new HashMap<String, String>();
    public static final HashMap<MapSerializer, Integer> EMPTY_REFERENCE_MAP   = new HashMap<MapSerializer, Integer>();
    public static final HashMap<Integer, MapSerializer> EMPTY_DEREFERENCE_MAP = new HashMap<Integer, MapSerializer>();

    private HashMap<MapSerializer, Integer>             _referenceMap;
    private HashMap<Integer, MapSerializer>             _dereferenceMap;

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
            for (Field f : object.getClass().getDeclaredFields())
            {
                f.setAccessible(true);
                DoubleParameter doubleParameter = f.getAnnotation(DoubleParameter.class);
                if (doubleParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.getDouble(object));
                }
                IntegerParameter intParameter = f.getAnnotation(IntegerParameter.class);
                if (intParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.getInt(object));
                }
                BooleanParameter boolParameter = f.getAnnotation(BooleanParameter.class);
                if (boolParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.getBoolean(object));
                }
                BrainTypeParameter brainTypeParameter = f.getAnnotation(BrainTypeParameter.class);
                if (brainTypeParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.get(object));
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
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
            for (Field f : object.getClass().getDeclaredFields())
            {
                f.setAccessible(true);
                String key = getKeyFromFieldName(f.getName());
                DoubleParameter doubleParameter = f.getAnnotation(DoubleParameter.class);
                if (doubleParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.setDouble(object, Double.parseDouble(map.get(key)));
                    }
                    else
                    {
                        f.setDouble(object, doubleParameter.defaultValue());
                    }
                }
                IntegerParameter intParameter = f.getAnnotation(IntegerParameter.class);
                if (intParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.setInt(object, Integer.parseInt(map.get(key)));
                    }
                    else
                    {
                        f.setInt(object, intParameter.defaultValue());
                    }
                }
                BooleanParameter boolParameter = f.getAnnotation(BooleanParameter.class);
                if (boolParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.setBoolean(object, Boolean.parseBoolean(map.get(key)));
                    }
                    else
                    {
                        f.setBoolean(object, boolParameter.defaultValue());
                    }
                }
                BrainTypeParameter brainTypeParameter = f.getAnnotation(BrainTypeParameter.class);
                if (brainTypeParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.set(object, BrainType.valueOf(map.get(key)));
                    }
                    else
                    {
                        f.set(object, brainTypeParameter.defaultValue());
                    }
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        object.finalizeDeserialization(map, EMPTY_DEREFERENCE_MAP);
    }
}
