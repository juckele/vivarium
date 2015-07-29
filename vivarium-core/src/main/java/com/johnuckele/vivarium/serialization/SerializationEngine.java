package com.johnuckele.vivarium.serialization;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.johnuckele.vivarium.core.Species;

public class SerializationEngine
{
    public static final HashMap<String, String>         EMPTY_OBJECT_MAP      = new HashMap<String, String>();
    public static final HashMap<Integer, MapSerializer> EMPTY_DEREFERENCE_MAP = new HashMap<Integer, MapSerializer>();

    private HashMap<MapSerializer, Integer>             _referenceMap;
    private HashMap<Integer, MapSerializer>             _dereferenceMap;

    public SerializationEngine(SerializationWorker worker)
    {
        _referenceMap = new HashMap<MapSerializer, Integer>();
        _dereferenceMap = new HashMap<Integer, MapSerializer>();
    }

    public MapSerializer deserialize(HashMap<String, String> map)
    {
        String type = map.get("+type");
        if (type.equals(Species.class.getSimpleName()))
        {
            Species s = Species.makeUninitializedSpeciesObject();
            deserialize(s, map);
            return s;
        }
        return null;
    }

    public HashMap<String, String> serialize(MapSerializer obj)
    {
        int id = _referenceMap.size();
        if (!_referenceMap.containsKey(obj))
        {
            _referenceMap.put(obj, id);
        }
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
        obj.finalizeSerialization(map, this._referenceMap);
        return map;
    }

    public static String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }

    public static void deserialize(MapSerializer obj, Map<String, String> map)
    {
        System.out.println("Deserializing an object");
        try
        {
            for (Field f : Species.class.getDeclaredFields())
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
