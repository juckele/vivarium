package com.johnuckele.vivarium.serialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.johnuckele.vivarium.audit.CensusFunction;
import com.johnuckele.vivarium.audit.CensusRecord;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;
import com.johnuckele.vivarium.core.brain.RandomBrain;

public class SerializationEngine
{
    public static final String ID_KEY = "+id";
    public static final String CATEGORY_KEY = "+category";
    public static final String CLASS_KEY = "+class";
    public static final HashMap<String, Object> EMPTY_OBJECT_MAP = new HashMap<String, Object>();
    public static final HashMap<SerializationCategory, HashMap<MapSerializer, Integer>> EMPTY_REFERENCE_MAP = new HashMap<SerializationCategory, HashMap<MapSerializer, Integer>>();
    public static final HashMap<SerializationCategory, HashMap<Integer, MapSerializer>> EMPTY_DEREFERENCE_MAP = new HashMap<SerializationCategory, HashMap<Integer, MapSerializer>>();

    private SerializedCollection _collection;
    private HashMap<SerializationCategory, HashMap<MapSerializer, Integer>> _referenceMap;
    private HashMap<SerializationCategory, HashMap<Integer, MapSerializer>> _dereferenceMap;

    public SerializationEngine()
    {
        _collection = new SerializedCollection();
        _referenceMap = new HashMap<SerializationCategory, HashMap<MapSerializer, Integer>>();
        _dereferenceMap = new HashMap<SerializationCategory, HashMap<Integer, MapSerializer>>();
    }

    @SuppressWarnings("unchecked")
    public MapSerializer deserializeMap(HashMap<String, Object> map)
    {
        map = (HashMap<String, Object>) map.clone();
        String clazzName = (String) map.remove(CLASS_KEY);
        map.remove(CATEGORY_KEY);
        int referenceID = (int) map.remove(ID_KEY);
        MapSerializer object = makeUninitializedMapSerializer(clazzName);
        deserialize(object, map);
        object.finalizeSerialization();
        storeIDToReference(referenceID, object);
        return object;
    }

    private MapSerializer makeUninitializedMapSerializer(String clazzName)
    {
        if (clazzName.equals(Species.class.getSimpleName()))
        {
            return Species.makeUninitialized();
        }
        else if (clazzName.equals(Blueprint.class.getSimpleName()))
        {
            return Blueprint.makeUninitialized();
        }
        else if (clazzName.equals(RandomBrain.class.getSimpleName()))
        {
            return RandomBrain.makeUninitialized();
        }
        else if (clazzName.equals(NeuralNetworkBrain.class.getSimpleName()))
        {
            return NeuralNetworkBrain.makeUninitialized();
        }
        else if (clazzName.equals(Creature.class.getSimpleName()))
        {
            return Creature.makeUninitialized();
        }
        else if (clazzName.equals(World.class.getSimpleName()))
        {
            return World.makeUninitialized();
        }
        else if (clazzName.equals(CensusFunction.class.getSimpleName()))
        {
            return CensusFunction.makeUninitialized();
        }
        else if (clazzName.equals(CensusRecord.class.getSimpleName()))
        {
            return CensusRecord.makeUninitialized();
        }
        else
        {
            throw new UnsupportedOperationException("Cannot deserialize class " + clazzName);
        }
    }

    /**
     * Creates a SerializedCollection object from a MapSerializer.
     *
     * @param object
     * @return
     */
    public SerializedCollection serialize(MapSerializer object)
    {
        _collection = new SerializedCollection();
        // Start by recursively serializing the top level object
        serializeObjectIntoCollection(object);
        return _collection;
    }

    private void serializeObjectIntoCollection(MapSerializer object)
    {
        try
        {
            SerializationCategory category = SerializationCategory.getCategoryForClass(object.getClass());
            if (!_referenceMap.containsKey(category) || !_referenceMap.get(category).containsKey(object))
            {
                // Serialize the current object
                int objectID = _collection.categoryCount(category);
                storeReferenceToID(object, objectID);
                HashMap<String, Object> map;
                map = serializeMapSerializer(object, objectID);
                _collection.addObject(map);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void storeReferenceToID(MapSerializer object, int objectID)
    {
        SerializationCategory category = SerializationCategory.getCategoryForClass(object.getClass());
        if (!_referenceMap.containsKey(category))
        {
            _referenceMap.put(category, new HashMap<MapSerializer, Integer>());
        }
        _referenceMap.get(category).put(object, objectID);
    }

    private void storeIDToReference(int objectID, MapSerializer object)
    {
        SerializationCategory category = SerializationCategory.getCategoryForClass(object.getClass());
        if (!_dereferenceMap.containsKey(category))
        {
            _dereferenceMap.put(category, new HashMap<Integer, MapSerializer>());
        }
        _dereferenceMap.get(category).put(objectID, object);
    }

    private Integer getReferenceID(MapSerializer object)
    {
        SerializationCategory serializationCategory = SerializationCategory.getCategoryForClass(object.getClass());
        HashMap<MapSerializer, Integer> category = _referenceMap.get(serializationCategory);
        if (category == null)
        {
            serializeObjectIntoCollection(object);
            category = _referenceMap.get(serializationCategory);
        }
        Integer id = category.get(object);
        if (id == null)
        {
            serializeObjectIntoCollection(object);
            id = category.get(object);
        }
        return id;
    }

    private MapSerializer getReferenceObject(SerializationCategory category, int objectID)
    {
        HashMap<Integer, MapSerializer> categoryMap = _dereferenceMap.get(category);
        return categoryMap.get(objectID);
    }

    @SuppressWarnings("unchecked")
    private Object serializeObject(Object object)
    {
        if (object == null)
        {
            return null;
        }

        Class<?> clazz = object.getClass();

        // Serialize value
        if (clazz.isArray())
        {
            // Do array crap here
            LinkedList<Object> list = new LinkedList<Object>();
            for (int i = 0; i < Array.getLength(object); i++)
            {
                Object arrayElement = Array.get(object, i);
                Object serializedElement = serializeObject(arrayElement);
                list.add(serializedElement);
            }
            return list;
        }
        else if (List.class.isAssignableFrom(clazz))
        {
            // Do list crap here
            LinkedList<Object> list = new LinkedList<Object>();
            for (Object element : (List<Object>) object)
            {
                list.add(serializeObject(element));
            }
            return list;
        }
        else if (MapSerializer.class.isAssignableFrom(clazz))
        {
            // Reference crap hereB
            return getReferenceID((MapSerializer) object);
        }
        else if (Enum.class.isAssignableFrom(clazz))
        {
            // Enum crap
            return "" + object;
        }
        else if (isPrimitive(clazz))
        {
            // Do nothing because this can be saved as is
            return object;
        }
        else
        {
            throw new UnsupportedOperationException("Cannot handle parameter type " + clazz);
        }
    }

    private HashMap<String, Object> serializeMapSerializer(MapSerializer object, int id) throws IllegalAccessException
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(ID_KEY, id);
        SerializationCategory serializationCategory = SerializationCategory.getCategoryForClass(object.getClass());
        map.put(CATEGORY_KEY, "" + serializationCategory);
        map.put(CLASS_KEY, "" + object.getClass().getSimpleName());
        try
        {
            for (Field f : getSerializedParameters(object))
            {
                Object valueObject = f.get(object);

                // Serialize value
                Object serializedValue = serializeObject(valueObject);

                // Add value to the map
                if (valueObject != null)
                {
                    map.put(f.getName().replaceAll("_", ""), serializedValue);
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

    private boolean isPrimitive(Class<?> clazz)
    {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Integer.class || clazz == Double.class;
    }

    private Set<Field> getSerializedParameters(MapSerializer object)
    {
        HashSet<Field> annotatedFields = new HashSet<Field>();
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                Annotation[] annotations = fields[i].getAnnotations();
                for (int j = 0; j < annotations.length; j++)
                {
                    if (annotations[j].annotationType() == SerializedParameter.class)
                    {
                        fields[i].setAccessible(true);
                        annotatedFields.add(fields[i]);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return annotatedFields;
    }

    public static String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }

    public void deserialize(MapSerializer object, Map<String, Object> map)
    {
        try
        {
            for (Field f : getSerializedParameters(object))
            {
                String attributeName = f.getName().replaceAll("_", "");
                if (map.containsKey(attributeName))
                {
                    Object valueObject = map.remove(attributeName);
                    Type fieldType = f.getGenericType();

                    valueObject = deserializeObject(valueObject, fieldType);

                    // Set value on the object
                    if (valueObject != null)
                    {
                        f.set(object, valueObject);
                    }
                }

            }
            if (!map.isEmpty())
            {
                throw new IllegalArgumentException("Map has unused keys and values of " + map + " when constructing "
                        + object.getClass().getSimpleName());
            }

        }
        catch (IllegalAccessException | NoSuchMethodException | SecurityException | InstantiationException
                | IllegalArgumentException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("unchecked")
    private Object deserializeObject(Object object, Type type) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class<?> clazz;
        if (type instanceof Class)
        {
            clazz = (Class<?>) type;
        }
        else if (type instanceof ParameterizedType)
        {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        else
        {
            throw new IllegalStateException("Unable to derive Class information from " + type);
        }
        // object.equals(null) is used for object based representations of null which could be passed to the
        // serialization engine.
        if (object == null)
        {
            return null;
        }

        // Deserialize value
        if (clazz.isArray())
        {
            // Do array crap here
            int size = ((List<Object>) object).size();
            Object array = Array.newInstance(clazz.getComponentType(), size);
            int i = 0;
            for (Object element : (List<Object>) object)
            {
                Object deserializedElement = deserializeObject(element, clazz.getComponentType());
                Array.set(array, i, deserializedElement);
                i++;
            }
            return array;
        }
        else if (List.class.isAssignableFrom(clazz))
        {
            // Do list crap here
            Constructor<?> listConstructor = clazz.getConstructor();
            List<Object> list = (List<Object>) listConstructor.newInstance();
            Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            for (Object element : (List<Object>) object)
            {
                Object deserializedElement = deserializeObject(element, elementType);
                list.add(deserializedElement);
            }
            return list;
        }
        else if (MapSerializer.class.isAssignableFrom(clazz))
        {
            // Reference crap here
            return getReferenceObject(SerializationCategory.getCategoryForClass(clazz), (Integer) object);
        }
        else if (Enum.class.isAssignableFrom(clazz))
        {
            // Enum crap
            @SuppressWarnings("rawtypes")
            Enum valueEnum = Enum.valueOf((Class<Enum>) clazz, "" + object);
            return valueEnum;
        }
        else if (isPrimitive(clazz))
        {
            // If this was saved or passed in as a primitive, but if it's in string form, we need to parse it
            if (object.getClass() == String.class)
            {
                return parsePrimitive(clazz, (String) object);
            }
            else
            {
                return object;
            }
        }
        else
        {
            throw new UnsupportedOperationException("Cannot handle parameter type " + clazz);
        }
    }

    private Object parsePrimitive(Class<?> clazz, String s)
    {
        if (clazz == Boolean.class || clazz == boolean.class)
        {
            return Boolean.parseBoolean(s);
        }
        else if (clazz == Integer.class || clazz == int.class)
        {
            return Integer.parseInt(s);
        }
        else if (clazz == Double.class || clazz == double.class)
        {
            return Double.parseDouble(s);
        }
        else
        {
            throw new UnsupportedOperationException("Unable to parse primitive " + clazz);
        }
    }

    /**
     * Creates a copy of a deserialized
     *
     * @param original
     * @return
     */
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
        _collection = collection;
        MapSerializer object = null;
        List<MapSerializer> objects = new LinkedList<MapSerializer>();
        for (SerializationCategory category : SerializationCategory.rankedValues())
        {
            // If a higher ranked category has objects in it, discard the contents of the
            // objects list by making a new list.
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
