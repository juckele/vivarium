package com.johnuckele.vivarium.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.core.Action;
import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Direction;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.Gender;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldBlueprint;
import com.johnuckele.vivarium.core.brain.Brain;
import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;
import com.johnuckele.vivarium.core.brain.RandomBrain;
import com.johnuckele.vivarium.util.HierarchicalListParser;

public class SerializationEngine
{
    public static final String                                                          ID_KEY                = "+id";
    public static final String                                                          CATEGORY_KEY          = "+category";
    public static final String                                                          CLASS_KEY             = "+class";
    public static final HashMap<String, Object>                                         EMPTY_OBJECT_MAP      = new HashMap<String, Object>();
    public static final HashMap<SerializationCategory, HashMap<MapSerializer, Integer>> EMPTY_REFERENCE_MAP   = new HashMap<SerializationCategory, HashMap<MapSerializer, Integer>>();
    public static final HashMap<SerializationCategory, HashMap<Integer, MapSerializer>> EMPTY_DEREFERENCE_MAP = new HashMap<SerializationCategory, HashMap<Integer, MapSerializer>>();

    private HashMap<SerializationCategory, HashMap<MapSerializer, Integer>> _referenceMap;
    private HashMap<SerializationCategory, HashMap<Integer, MapSerializer>> _dereferenceMap;

    public SerializationEngine()
    {
        _referenceMap = new HashMap<SerializationCategory, HashMap<MapSerializer, Integer>>();
        _dereferenceMap = new HashMap<SerializationCategory, HashMap<Integer, MapSerializer>>();
    }

    public MapSerializer deserializeMap(HashMap<String, Object> map)
    {
        String clazzName = (String) map.get(CLASS_KEY);
        MapSerializer object;
        if (clazzName.equals(Species.class.getSimpleName()))
        {
            object = Species.makeUninitialized();
        }
        else if (clazzName.equals(WorldBlueprint.class.getSimpleName()))
        {
            object = WorldBlueprint.makeUninitialized();
        }
        else if (clazzName.equals(RandomBrain.class.getSimpleName()))
        {
            object = RandomBrain.makeUninitialized();
        }
        else if (clazzName.equals(NeuralNetworkBrain.class.getSimpleName()))
        {
            object = NeuralNetworkBrain.makeUninitialized();
        }
        else if (clazzName.equals(Creature.class.getSimpleName()))
        {
            object = Creature.makeUninitialized();
        }
        else if (clazzName.equals(World.class.getSimpleName()))
        {
            object = World.makeUninitialized();
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
            HashMap<String, Object> map = serializeObject(object, objectID);
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
        HashMap<Integer, MapSerializer> categoryMap = _dereferenceMap.get(category);
        return categoryMap.get(objectID);
    }

    private HashMap<String, Object> serializeObject(MapSerializer object, int id)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
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
                if (parameterClazz == ArrayList.class)
                {
                    ArrayList<?> valueArray = (ArrayList<?>) valueObject;
                    ArrayList<Integer> referenceArray = new ArrayList<Integer>();
                    for (Object reference : valueArray)
                    {
                        referenceArray.add(getReferenceID((MapSerializer) reference));
                    }
                    valueObject = referenceArray;
                }
                else if (parameterClazz == Species.class)
                {
                    valueObject = "" + getReferenceID((MapSerializer) valueObject);
                }
                else if (parameterClazz == Brain.class)
                {
                    valueObject = "" + getReferenceID((MapSerializer) valueObject);
                }
                else if (parameterClazz == WorldBlueprint.class)
                {
                    valueObject = "" + getReferenceID((MapSerializer) valueObject);
                }
                else if (parameterClazz == Creature.class)
                {
                    if (valueObject != null)
                    {
                        valueObject = "" + getReferenceID((MapSerializer) valueObject);
                    }
                    else
                    {
                        valueObject = "";
                    }
                }
                else if (parameterClazz == Boolean.class)
                {
                    valueObject = "" + valueObject;
                }
                else if (parameterClazz == BrainType.class)
                {
                    valueObject = ((BrainType) valueObject).name();
                }
                else if (parameterClazz == Gender.class)
                {
                    valueObject = ((Gender) valueObject).name();
                }
                else if (parameterClazz == Direction.class)
                {
                    valueObject = ((Direction) valueObject).name();
                }
                else if (parameterClazz == Action.class)
                {
                    valueObject = ((Action) valueObject).name();
                }
                else if (parameterClazz == Double.class)
                {
                    valueObject = "" + valueObject;
                }
                else if (parameterClazz == Integer.class)
                {
                    valueObject = "" + valueObject;
                }
                else if (parameterClazz == double[].class)
                {
                    double[] valueArray = (double[]) valueObject;
                    List<Object> valueList = new LinkedList<Object>();
                    for (double i : valueArray)
                    {
                        valueList.add(i);
                    }
                    valueObject = valueList;
                }
                else if (parameterClazz == double[][][].class)
                {
                    double[][][] valueArray = (double[][][]) valueObject;
                    List<Object> outerValueList = new LinkedList<Object>();
                    for (double[][] i : valueArray)
                    {
                        List<Object> valueList = new LinkedList<Object>();
                        outerValueList.add(valueList);
                        for (double[] j : i)
                        {
                            List<Object> innerValueList = new LinkedList<Object>();
                            valueList.add(innerValueList);
                            for (double k : j)
                            {
                                innerValueList.add(k);
                            }
                        }
                    }
                    valueObject = outerValueList;
                }
                else if (parameterClazz == EntityType[][].class)
                {
                    EntityType[][] valueArray = (EntityType[][]) valueObject;
                    List<Object> outerValueList = new LinkedList<Object>();
                    for (EntityType[] i : valueArray)
                    {
                        List<Object> valueList = new LinkedList<Object>();
                        outerValueList.add(valueList);
                        for (EntityType j : i)
                        {
                            valueList.add(valueObject = (j).name());
                        }
                    }
                    valueObject = outerValueList;
                }
                else if (parameterClazz == Creature[][].class)
                {
                    Creature[][] valueArray = (Creature[][]) valueObject;
                    List<Object> outerValueList = new LinkedList<Object>();
                    for (Creature[] i : valueArray)
                    {
                        List<Object> valueList = new LinkedList<Object>();
                        outerValueList.add(valueList);
                        for (Creature j : i)
                        {
                            if (j != null)
                            {
                                valueList.add(getReferenceID(j));
                            }
                            else
                            {
                                valueList.add("");
                            }
                        }
                    }
                    valueObject = outerValueList;
                }
                else
                {
                    throw new UnsupportedOperationException("Cannot handle parameter type " + parameterClazz);
                }

                // Add value to the map
                if (valueObject != null)
                {
                    map.put(parameter.getName(), valueObject);
                }
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

    public void deserialize(MapSerializer object, Map<String, Object> map)
    {
        try
        {
            for (SerializedParameter parameter : object.getMappedParameters())
            {
                // Determine string to deserialize (default vs in map value)
                String valueString = null;
                Object valueObject = null;
                if (map.containsKey(parameter.getName()))
                {
                    if (map.get(parameter.getName()) instanceof String)
                    {
                        valueString = (String) map.get(parameter.getName());
                    }
                    else
                    {
                        valueObject = map.get(parameter.getName());
                    }
                }
                else
                {
                    valueString = parameter.getDefaultValue();
                }

                // Deserialize value
                Class<?> parameterClazz = parameter.getValueClass();
                if (parameterClazz == ArrayList.class)
                {
                    List<Object> referenceList;
                    if (valueString != null)
                    {
                        referenceList = HierarchicalListParser.parseList(valueString);
                    }
                    else
                    {
                        referenceList = (List<Object>) valueObject;
                    }
                    ArrayList<Object> valueList = new ArrayList<Object>();
                    for (Object reference : referenceList)
                    {
                        int referenceID;
                        if (reference instanceof String)
                        {
                            referenceID = Integer.parseInt((String) reference);
                        }
                        else
                        {
                            referenceID = (int) reference;
                        }
                        valueList.add(getReferenceObject(parameter.getReferenceCategory(), referenceID));
                    }
                    valueObject = valueList;
                }
                else if (parameterClazz == Species.class)
                {
                    valueObject = getReferenceObject(parameter.getReferenceCategory(), Integer.parseInt(valueString));
                }
                else if (parameterClazz == Brain.class)
                {
                    valueObject = getReferenceObject(parameter.getReferenceCategory(), Integer.parseInt(valueString));
                }
                else if (parameterClazz == WorldBlueprint.class)
                {
                    valueObject = getReferenceObject(parameter.getReferenceCategory(), Integer.parseInt(valueString));
                }
                else if (parameterClazz == Creature.class)
                {
                    if (!valueString.equals(""))
                    {
                        valueObject = getReferenceObject(parameter.getReferenceCategory(),
                                Integer.parseInt(valueString));
                    }
                    else
                    {
                        valueObject = null;
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
                else if (parameterClazz == Gender.class)
                {
                    valueObject = Gender.valueOf(valueString);
                }
                else if (parameterClazz == Direction.class)
                {
                    valueObject = Direction.valueOf(valueString);
                }
                else if (parameterClazz == Action.class)
                {
                    valueObject = Action.valueOf(valueString);
                }
                else if (parameterClazz == Double.class)
                {
                    if (valueString != null)
                    {
                        valueObject = Double.parseDouble(valueString);
                    }
                }
                else if (parameterClazz == Integer.class)
                {
                    valueObject = Integer.parseInt(valueString);
                }
                else if (parameterClazz == double[].class)
                {
                    List<Object> valueList = (List<Object>) valueObject;
                    double[] valueArray = new double[valueList.size()];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
                        valueArray[i] = (double) objectI;
                        i++;
                    }
                    valueObject = valueArray;
                }
                else if (parameterClazz == double[][][].class)
                {
                    List<Object> valueList = (List<Object>) valueObject;
                    double[][][] valueArray = new double[valueList.size()][][];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
                        @SuppressWarnings("unchecked")
                        List<Object> listI = (List<Object>) objectI;
                        valueArray[i] = new double[listI.size()][];
                        int j = 0;
                        for (Object objectJ : listI)
                        {
                            @SuppressWarnings("unchecked")
                            List<Object> listJ = (List<Object>) objectJ;
                            valueArray[i][j] = new double[listJ.size()];
                            int k = 0;
                            for (Object objectK : listJ)
                            {
                                valueArray[i][j][k] = (double) objectK;
                                k++;
                            }
                            j++;
                        }
                        i++;
                    }
                    valueObject = valueArray;
                }
                else if (parameterClazz == EntityType[][].class)
                {
                    List<Object> valueList = HierarchicalListParser.parseList(valueString);
                    EntityType[][] valueArray = new EntityType[valueList.size()][];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
                        @SuppressWarnings("unchecked")
                        List<Object> listI = (List<Object>) objectI;
                        valueArray[i] = new EntityType[listI.size()];
                        int j = 0;
                        for (Object objectJ : listI)
                        {
                            valueArray[i][j] = EntityType.valueOf((String) objectJ);
                            j++;
                        }
                        i++;
                    }
                    valueObject = valueArray;
                }
                else if (parameterClazz == Creature[][].class)
                {
                    List<Object> valueList = HierarchicalListParser.parseList(valueString);
                    Creature[][] valueArray = new Creature[valueList.size()][];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
                        @SuppressWarnings("unchecked")
                        List<Object> listI = (List<Object>) objectI;
                        valueArray[i] = new Creature[listI.size()];
                        int j = 0;
                        for (Object objectJ : listI)
                        {
                            String stringJ = (String) objectJ;
                            if (!stringJ.equals(""))
                            {
                                valueArray[i][j] = (Creature) getReferenceObject(parameter.getReferenceCategory(),
                                        Integer.parseInt(stringJ));
                            }
                            else
                            {
                                valueArray[i][j] = null;
                            }
                            j++;
                        }
                        i++;
                    }
                    valueObject = valueArray;
                }
                else
                {
                    throw new UnsupportedOperationException("Cannot handle parameter type " + parameterClazz);
                }

                // Set value on the object
                if (valueObject != null)
                {
                    object.setValue(parameter.getName(), valueObject);
                }
                else if (valueString != null)
                {
                    object.setValue(parameter.getName(), valueString);
                }
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
                HashMap<String, Object> map = collection.popNext(category);
                object = deserializeMap(map);
                int referenceID = Integer.parseInt((String) map.get(ID_KEY));
                storeIDToReference(referenceID, object);
            }
        }
        return object;
    }
}
