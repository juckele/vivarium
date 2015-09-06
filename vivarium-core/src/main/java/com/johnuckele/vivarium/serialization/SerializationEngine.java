package com.johnuckele.vivarium.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.audit.AuditFunction;
import com.johnuckele.vivarium.audit.AuditRecord;
import com.johnuckele.vivarium.audit.AuditType;
import com.johnuckele.vivarium.audit.CensusFunction;
import com.johnuckele.vivarium.audit.CensusRecord;
import com.johnuckele.vivarium.core.Action;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Direction;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.Gender;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
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

    @SuppressWarnings("unchecked")
    public MapSerializer deserializeMap(HashMap<String, Object> map)
    {
        map = (HashMap<String, Object>) map.clone();
        String clazzName = (String) map.remove(CLASS_KEY);
        map.remove(CATEGORY_KEY);
        int referenceID = Integer.parseInt((String) map.remove(ID_KEY));
        MapSerializer object;
        if (clazzName.equals(Species.class.getSimpleName()))
        {
            object = Species.makeUninitialized();
        }
        else if (clazzName.equals(Blueprint.class.getSimpleName()))
        {
            object = Blueprint.makeUninitialized();
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
        else if (clazzName.equals(CensusFunction.class.getSimpleName()))
        {
            object = CensusFunction.makeUninitialized();
        }
        else if (clazzName.equals(CensusRecord.class.getSimpleName()))
        {
            object = CensusRecord.makeUninitialized();
        }
        else
        {
            throw new UnsupportedOperationException("Cannot deserialize class " + clazzName);
        }
        deserialize(object, map);
        storeIDToReference(referenceID, object);
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
                    ArrayList<String> referenceArray = new ArrayList<String>();
                    if (parameter.hasReferenceCategory())
                    {
                        for (Object reference : valueArray)
                        {
                            referenceArray.add("" + getReferenceID((MapSerializer) reference));
                        }

                    }
                    else
                    {
                        for (Object reference : valueArray)
                        {
                            referenceArray.add("" + reference);
                        }
                    }
                    valueObject = referenceArray;
                }
                else if (parameterClazz == AuditFunction.class)
                {
                    valueObject = "" + getReferenceID((MapSerializer) valueObject);
                }
                else if (parameterClazz == Species.class)
                {
                    valueObject = "" + getReferenceID((MapSerializer) valueObject);
                }
                else if (parameterClazz == Brain.class)
                {
                    valueObject = "" + getReferenceID((MapSerializer) valueObject);
                }
                else if (parameterClazz == Blueprint.class)
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
                else if (parameterClazz == AuditType.class)
                {
                    valueObject = ((AuditType) valueObject).name();
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
                        valueList.add("" + i);
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
                                innerValueList.add("" + k);
                            }
                        }
                    }
                    valueObject = outerValueList;
                }
                else if (parameterClazz == AuditRecord[].class)
                {
                    AuditRecord[] valueArray = (AuditRecord[]) valueObject;
                    List<Object> valueList = new LinkedList<Object>();
                    for (AuditRecord i : valueArray)
                    {
                        if (i != null)
                        {
                            valueList.add("" + getReferenceID(i));
                        }
                        else
                        {
                            valueList.add("");
                        }
                    }
                    valueObject = valueList;
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
                                valueList.add("" + getReferenceID(j));
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
        catch (

        IllegalArgumentException e)

        {
            e.printStackTrace();
        }
        return map;

    }

    public static String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }

    @SuppressWarnings("unchecked")
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
                        valueString = (String) map.remove(parameter.getName());
                    }
                    else
                    {
                        valueObject = map.remove(parameter.getName());
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
                    if (parameter.hasReferenceCategory())
                    {
                        for (Object reference : referenceList)
                        {
                            int referenceID;
                            referenceID = Integer.parseInt((String) reference);
                            valueList.add(getReferenceObject(parameter.getReferenceCategory(), referenceID));
                        }
                    }
                    else
                    {
                        for (Object reference : referenceList)
                        {
                            valueList.add("" + reference);
                        }
                    }
                    valueObject = valueList;
                }
                else if (parameterClazz == AuditFunction.class)
                {
                    valueObject = getReferenceObject(parameter.getReferenceCategory(), Integer.parseInt(valueString));
                }
                else if (parameterClazz == Species.class)
                {
                    valueObject = getReferenceObject(parameter.getReferenceCategory(), Integer.parseInt(valueString));
                }
                else if (parameterClazz == Brain.class)
                {
                    valueObject = getReferenceObject(parameter.getReferenceCategory(), Integer.parseInt(valueString));
                }
                else if (parameterClazz == Blueprint.class)
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
                        valueString = null;
                        valueObject = null;
                    }
                }
                else if (parameterClazz == Boolean.class)
                {
                    valueObject = Boolean.parseBoolean(valueString);
                }
                else if (parameterClazz == AuditType.class)
                {
                    valueObject = AuditType.valueOf(valueString);
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
                        valueArray[i] = Double.parseDouble((String) objectI);
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
                        List<Object> listI = (List<Object>) objectI;
                        valueArray[i] = new double[listI.size()][];
                        int j = 0;
                        for (Object objectJ : listI)
                        {
                            List<Object> listJ = (List<Object>) objectJ;
                            valueArray[i][j] = new double[listJ.size()];
                            int k = 0;
                            for (Object objectK : listJ)
                            {
                                valueArray[i][j][k] = Double.parseDouble((String) objectK);
                                k++;
                            }
                            j++;
                        }
                        i++;
                    }
                    valueObject = valueArray;
                }
                else if (parameterClazz == AuditRecord[].class)
                {
                    List<Object> valueList = (List<Object>) valueObject;
                    AuditRecord[] valueArray = new AuditRecord[valueList.size()];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
                        String stringI = "" + objectI;

                        if (!stringI.equals(""))
                        {
                            valueArray[i] = (AuditRecord) getReferenceObject(parameter.getReferenceCategory(),
                                    Integer.parseInt(stringI));
                        }
                        else
                        {
                            valueArray[i] = null;
                        }
                        i++;
                    }
                    valueObject = valueArray;
                }
                else if (parameterClazz == EntityType[][].class)
                {
                    List<Object> valueList = (List<Object>) valueObject;
                    EntityType[][] valueArray = new EntityType[valueList.size()][];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
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
                    List<Object> valueList = (List<Object>) valueObject;
                    Creature[][] valueArray = new Creature[valueList.size()][];
                    int i = 0;
                    for (Object objectI : valueList)
                    {
                        List<Object> listI = (List<Object>) objectI;
                        valueArray[i] = new Creature[listI.size()];
                        int j = 0;
                        for (Object objectJ : listI)
                        {
                            String stringJ = "" + objectJ;

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
            if (!map.isEmpty())
            {
                throw new IllegalArgumentException("Map has unused keys and values of " + map + " when constructing "
                        + object.getClass().getSimpleName());
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

    /**
     * Returns a single MapSerializer object from a serialized collection. In a serialized collection with more than one
     * serialized object serialized, the object returned will be the object with the highest relative serialization
     * category ranking. If the collection is empty or if there are multiple objects with the same highest serialized
     * category ranking, this method throws an IllegalStateException.
     *
     * @param collection
     * @return A single MapSerializer object represented in the collection
     * @throws IllegalStateException
     *             If there is not a single object in the collection which holds the highest relative serialization
     *             category ranking.
     */
    public MapSerializer deserialize(SerializedCollection collection) throws IllegalStateException
    {
        List<MapSerializer> objects = deserializeList(collection);
        if (objects.size() == 1)
        {
            return objects.get(0);
        }
        else if (objects.size() == 0)
        {
            throw new IllegalStateException("SerializedCollection has no serialized objects.");
        }
        else
        {
            throw new IllegalStateException(
                    "SerializedCollection has a tie for highest ranked serialized objects, please use deserializeList instead.");
        }
    }

    /**
     * Returns a List of MapSerializer objects from a serialized collection. The objects returned will be the objects
     * with the highest relative serialization category ranking.
     *
     * @param collection
     * @return
     */
    public List<MapSerializer> deserializeList(SerializedCollection collection)
    {
        MapSerializer object = null;
        List<MapSerializer> objects = new LinkedList<MapSerializer>();
        for (SerializationCategory category : SerializationCategory.rankedValues())
        {
            if (collection.hasNext(category))
            {
                objects = new LinkedList<MapSerializer>();
            }
            while (collection.hasNext(category))
            {
                HashMap<String, Object> map = collection.popNext(category);
                object = deserializeMap(map);
                objects.add(object);
            }
        }
        return objects;
    }
}
