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

    public SerializedCollection serialize(MapSerializer obj)
    {
        SerializedCollection collection = new SerializedCollection();
        // Serialize all references first
        for (MapSerializer reference : obj.getReferences())
        {
            if (!_referenceMap.containsKey(reference))
            {
                HashMap<String, String> map = serializeObject(reference, 0);
                collection.addObject("Species", map);
            }
        }
        // Serialize the top level object
        HashMap<String, String> map = serializeObject(obj, 0);
        collection.addObject("WorldBlueprint", map);

        return collection;
    }

    public HashMap<String, String> serializeObject(MapSerializer obj, int id)
    {
        System.out.println("Serializing... " + obj);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("+id", "" + id);
        map.put("+type", "" + obj.getClass().getSimpleName());
        try
        {
            for (Field f : obj.getClass().getDeclaredFields())
            {
                f.setAccessible(true);
                DoubleParameter doubleParameter = f.getAnnotation(DoubleParameter.class);
                if (doubleParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.getDouble(obj));
                }
                IntegerParameter intParameter = f.getAnnotation(IntegerParameter.class);
                if (intParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.getInt(obj));
                }
                BooleanParameter boolParameter = f.getAnnotation(BooleanParameter.class);
                if (boolParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.getBoolean(obj));
                }
                BrainTypeParameter brainTypeParameter = f.getAnnotation(BrainTypeParameter.class);
                if (brainTypeParameter != null)
                {
                    map.put(getKeyFromFieldName(f.getName()), "" + f.get(obj));
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
        obj.finalizeSerialization(map, EMPTY_REFERENCE_MAP);
        return map;
    }

    public static String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }

    public static void deserialize(MapSerializer obj, Map<String, String> map)
    {
        System.out.println("Deserializing an object " + obj);
        try
        {
            for (Field f : obj.getClass().getDeclaredFields())
            {
                f.setAccessible(true);
                String key = getKeyFromFieldName(f.getName());
                DoubleParameter doubleParameter = f.getAnnotation(DoubleParameter.class);
                if (doubleParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.setDouble(obj, Double.parseDouble(map.get(key)));
                    }
                    else
                    {
                        f.setDouble(obj, doubleParameter.defaultValue());
                    }
                }
                IntegerParameter intParameter = f.getAnnotation(IntegerParameter.class);
                if (intParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.setInt(obj, Integer.parseInt(map.get(key)));
                    }
                    else
                    {
                        f.setInt(obj, intParameter.defaultValue());
                    }
                }
                BooleanParameter boolParameter = f.getAnnotation(BooleanParameter.class);
                if (boolParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.setBoolean(obj, Boolean.parseBoolean(map.get(key)));
                    }
                    else
                    {
                        f.setBoolean(obj, boolParameter.defaultValue());
                    }
                }
                BrainTypeParameter brainTypeParameter = f.getAnnotation(BrainTypeParameter.class);
                if (brainTypeParameter != null)
                {
                    if (map.containsKey(key))
                    {
                        f.set(obj, BrainType.valueOf(map.get(key)));
                    }
                    else
                    {
                        f.set(obj, brainTypeParameter.defaultValue());
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
        obj.finalizeDeserialization(map, EMPTY_DEREFERENCE_MAP);
    }
}
