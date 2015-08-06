package com.johnuckele.vivarium.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.WorldBlueprint;
import com.johnuckele.vivarium.core.brain.BrainType;

public class SerializationEngine
{
    public static final String                                                          ID_KEY                = "+id";
    public static final String                                                          CATEGORY_KEY          = "+category";
    public static final String                                                          CLASS_KEY             = "+class";
    public static final HashMap<String, String>                                         EMPTY_OBJECT_MAP      = new HashMap<String, String>();
    public static final HashMap<SerializationCategory, HashMap<MapSerializer, Integer>> EMPTY_REFERENCE_MAP   = new HashMap<SerializationCategory, HashMap<MapSerializer, Integer>>();
    public static final HashMap<SerializationCategory, HashMap<Integer, MapSerializer>> EMPTY_DEREFERENCE_MAP = new HashMap<SerializationCategory, HashMap<Integer, MapSerializer>>();

    private HashMap<SerializationCategory, HashMap<MapSerializer, Integer>> _referenceMap;
    private HashMap<SerializationCategory, HashMap<Integer, MapSerializer>> _dereferenceMap;

    public SerializationEngine()
    {
        _referenceMap = new HashMap<SerializationCategory, HashMap<MapSerializer, Integer>>();
        _dereferenceMap = new HashMap<SerializationCategory, HashMap<Integer, MapSerializer>>();
    }

    public MapSerializer deserializeMap(HashMap<String, String> map)
    {
        String clazzName = map.get(CLASS_KEY);
        MapSerializer object;
        if (clazzName.equals(Species.class.getSimpleName()))
        {
            object = Species.makeUninitialized();
        }
        else if (clazzName.equals(WorldBlueprint.class.getSimpleName()))
        {
            object = WorldBlueprint.makeUninitialized();
        }
        else
        {
            throw new UnsupportedOperationException("Cannot deserialize class " + clazzName);
        }
        deserialize(object, map);
        return object;
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
        if (!_referenceMap.containsKey(object.getSerializationCategory())
                || !_referenceMap.get(object.getSerializationCategory()).containsKey(object))
        {
            // Serialize all references first
            for (MapSerializer reference : object.getReferences())
            {
                serializeObjectIntoCollection(reference, collection);
            }
            // Serialize the current object
            int objectID = collection.categoryCount(object.getSerializationCategory());
            System.out.println("ID " + objectID);
            storeReferenceToID(object, objectID);
            HashMap<String, String> map = serializeObject(object, objectID);
            collection.addObject(map);
        }
    }

    private void storeReferenceToID(MapSerializer object, int objectID)
    {
        if (!_referenceMap.containsKey(object.getSerializationCategory()))
        {
            _referenceMap.put(object.getSerializationCategory(), new HashMap<MapSerializer, Integer>());
        }
        _referenceMap.get(object.getSerializationCategory()).put(object, objectID);
    }

    private void storeIDToReference(int objectID, MapSerializer object)
    {
        if (!_dereferenceMap.containsKey(object.getSerializationCategory()))
        {
            _dereferenceMap.put(object.getSerializationCategory(), new HashMap<Integer, MapSerializer>());
        }
        _dereferenceMap.get(object.getSerializationCategory()).put(objectID, object);
    }

    private Integer getReferenceID(MapSerializer object)
    {
        return _referenceMap.get(object.getSerializationCategory()).get(object);
    }

    private MapSerializer getReferenceObject(SerializationCategory category, int objectID)
    {
        return _dereferenceMap.get(category).get(objectID);
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
                // Get the value
                Object valueObject = object.getValue(parameter.getName());

                // Serialize value
                Class<?> parameterClazz = parameter.getValueClass();
                String valueString = null;
                if (parameterClazz == ArrayList.class)
                {
                    ArrayList<?> valueArray = (ArrayList) valueObject;
                    ArrayList<Integer> referenceArray = new ArrayList<Integer>();
                    SerializationCategory referenceCategory = parameter.getReferenceCategory();
                    for (Object reference : valueArray)
                    {
                        referenceArray.add(getReferenceID((MapSerializer) reference));
                    }
                    valueString = referenceArray.toString();
                }
                else if (parameterClazz == Boolean.class)
                {
                    valueString = "" + valueObject;
                }
                else if (parameterClazz == BrainType.class)
                {
                    valueString = ((BrainType) valueObject).name();
                }
                else if (parameterClazz == Double.class)
                {
                    valueString = "" + valueObject;
                }
                else if (parameterClazz == Integer.class)
                {
                    valueString = "" + valueObject;
                }
                else
                {
                    throw new UnsupportedOperationException("Cannot handle parameter type " + parameterClazz);
                }

                // Add value to the map
                map.put(parameter.getName(), valueString);
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        return map;
    }

    public static String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }

    public void deserialize(MapSerializer object, Map<String, String> map)
    {
        System.out.println("Deserializing an object " + object);
        try
        {
            for (SerializedParameter parameter : object.getMappedParameters())
            {
                // Determine string to deserialize (default vs in map value)
                String valueString;
                if (map.containsKey(parameter.getName()))
                {
                    valueString = map.get(parameter.getName());
                }
                else
                {
                    valueString = parameter.getDefaultValue();
                }

                // Deserialize value
                Class<?> parameterClazz = parameter.getValueClass();
                Object valueObject = null;
                if (parameterClazz == ArrayList.class)
                {
                    valueObject = new ArrayList<Object>();
                    ArrayList<MapSerializer> valueArray = new ArrayList<MapSerializer>();
                    SerializationCategory referenceCategory = parameter.getReferenceCategory();
                    String[] referenceStrings = valueString.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                    if (referenceStrings.length != 1 || !referenceStrings[0].equals(""))
                    {
                        for (String referenceString : referenceStrings)
                        {
                            valueArray.add(getReferenceObject(parameter.getReferenceCategory(),
                                    Integer.parseInt(referenceString)));
                        }
                    }
                }
                else if (parameterClazz == Boolean.class)
                {
                    valueObject = Boolean.parseBoolean(valueString);
                }
                else if (parameterClazz == BrainType.class)
                {
                    valueObject = BrainType.valueOf(valueString);
                }
                else if (parameterClazz == Double.class)
                {
                    valueObject = Double.parseDouble(valueString);
                }
                else if (parameterClazz == Integer.class)
                {
                    valueObject = Integer.parseInt(valueString);
                }
                else
                {
                    throw new UnsupportedOperationException("Cannot handle parameter type " + parameterClazz);
                }

                // Set value on the object
                object.setValue(parameter.getName(), valueObject);
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public MapSerializer makeCopy(MapSerializer original)
    {
        SerializedCollection collection = serialize(original);
        MapSerializer copy = deserialize(collection);
        return copy;
    }

    private MapSerializer deserialize(SerializedCollection collection)
    {
        MapSerializer object = null;
        for (SerializationCategory category : SerializationCategory.rankedValues())
        {
            while (collection.hasNext(category))
            {
                HashMap<String, String> map = collection.popNext(category);
                object = deserializeMap(map);
                storeIDToReference(Integer.parseInt(map.get(ID_KEY)), object);
            }
        }
        return object;
    }
}
