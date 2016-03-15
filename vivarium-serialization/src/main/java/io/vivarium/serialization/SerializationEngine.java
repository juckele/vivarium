package io.vivarium.serialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.vivarium.audit.ActionFrequencyFunction;
import io.vivarium.audit.ActionFrequencyRecord;
import io.vivarium.audit.CensusFunction;
import io.vivarium.audit.CensusRecord;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.core.processor.NeuralNetwork;
import io.vivarium.core.processor.ProcessorBlueprint;
import io.vivarium.core.processor.RandomGenerator;
import io.vivarium.util.UUID;

public class SerializationEngine
{
    public static final String ID_KEY = "uuid";
    public static final String CLASS_KEY = "+class";
    public static final Map<String, Object> EMPTY_OBJECT_MAP = Collections.unmodifiableMap(new HashMap<>());
    public static final Map<VivariumObject, UUID> EMPTY_REFERENCE_MAP = Collections.unmodifiableMap(new HashMap<>());
    public static final Map<UUID, VivariumObject> EMPTY_DEREFERENCE_MAP = Collections.unmodifiableMap(new HashMap<>());

    private MapCollection _collection;
    private HashMap<VivariumObject, UUID> _referenceMap;
    private HashMap<UUID, VivariumObject> _dereferenceMap;

    public SerializationEngine()
    {
        _collection = new MapCollection();
        _referenceMap = new HashMap<>();
        _dereferenceMap = new HashMap<>();
    }

    public void preDeserializeMap(HashMap<String, Object> map)
    {
        // Create an object
        String clazzName = (String) map.get(CLASS_KEY);
        VivariumObject object = makeUninitializedMapSerializer(clazzName);

        // And store it into the idToReference map to allow later use (and circular references)
        UUID uuid = UUID.fromString((String) map.get(ID_KEY));
        storeIDToReference(uuid, object);
    }

    @SuppressWarnings("unchecked")
    public VivariumObject deserializeMap(HashMap<String, Object> map)
    {
        // Copy the map and remove the class meta-parameter (leaving this on would cause later completeness checks to
        // fail)
        map = (HashMap<String, Object>) map.clone();
        map.remove(CLASS_KEY);

        // Locate the partially instantiated object created by the preDeserializeMap pass
        UUID uuid = UUID.fromString((String) map.get(ID_KEY));
        VivariumObject object = this.getReferenceObject(uuid);

        // Deserialize the object
        deserialize(object, map);
        object.finalizeSerialization();

        return object;
    }

    private VivariumObject makeUninitializedMapSerializer(String clazzName)
    {
        try
        {
            Constructor<?> constructor;
            if (clazzName.equals(ProcessorBlueprint.class.getSimpleName()))
            {
                constructor = ProcessorBlueprint.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(Species.class.getSimpleName()))
            {
                constructor = Species.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(Blueprint.class.getSimpleName()))
            {
                constructor = Blueprint.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(RandomGenerator.class.getSimpleName()))
            {
                constructor = RandomGenerator.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(NeuralNetwork.class.getSimpleName()))
            {
                constructor = NeuralNetwork.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(Creature.class.getSimpleName()))
            {
                constructor = Creature.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(World.class.getSimpleName()))
            {
                constructor = World.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(CensusFunction.class.getSimpleName()))
            {
                constructor = CensusFunction.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(CensusRecord.class.getSimpleName()))
            {
                constructor = CensusRecord.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(ActionFrequencyFunction.class.getSimpleName()))
            {
                constructor = ActionFrequencyFunction.class.getDeclaredConstructor();
            }
            else if (clazzName.equals(ActionFrequencyRecord.class.getSimpleName()))
            {
                constructor = ActionFrequencyRecord.class.getDeclaredConstructor();
            }
            else
            {
                throw new UnsupportedOperationException("Cannot deserialize class " + clazzName);
            }
            constructor.setAccessible(true);
            return (VivariumObject) constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e)
        {
            e.printStackTrace();
            throw new RuntimeException(
                    "Reflection during serialization failed. Unable to create new instance of " + clazzName + ". ", e);
        }
    }

    /**
     * Creates a SerializedCollection object from a MapSerializer.
     *
     * @param object
     * @return
     */
    public MapCollection serialize(VivariumObject object)
    {
        _collection = new MapCollection();
        // Start by recursively serializing the top level object
        serializeObjectIntoCollection(object);
        return _collection;
    }

    /**
     * Creates a SerializedCollection object from a MapSerializerCollection.
     *
     * @param object
     * @return
     */
    public MapCollection serialize(VivariumObjectCollection collection)
    {
        _collection = new MapCollection();
        List<VivariumObject> list = collection.getAll(VivariumObject.class);
        for (VivariumObject object : list)
        {
            serializeObjectIntoCollection(object);
        }
        return _collection;
    }

    private void serializeObjectIntoCollection(VivariumObject object)
    {
        try
        {
            if (!_referenceMap.containsKey(object))
            {
                storeReferenceToID(object);
                HashMap<String, Object> map;
                map = serializeMapSerializer(object);
                _collection.addObject(map);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void storeReferenceToID(VivariumObject object)
    {
        _referenceMap.put(object, object.getUUID());
    }

    private void storeIDToReference(UUID uuid, VivariumObject object)
    {
        _dereferenceMap.put(uuid, object);
    }

    private UUID getReferenceID(VivariumObject object)
    {
        return _referenceMap.get(object);
    }

    private VivariumObject getReferenceObject(UUID uuid)
    {
        return _dereferenceMap.get(uuid);
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
            LinkedList<Object> list = new LinkedList<>();
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
            LinkedList<Object> list = new LinkedList<>();
            for (Object element : (List<Object>) object)
            {
                list.add(serializeObject(element));
            }
            return list;
        }
        else if (VivariumObject.class.isAssignableFrom(clazz))
        {
            // Reference crap here
            serializeObjectIntoCollection((VivariumObject) object);
            return getReferenceID((VivariumObject) object).toString();
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
        else if (clazz == UUID.class)
        {
            return object.toString();
        }
        else
        {
            throw new UnsupportedOperationException("Cannot handle parameter type " + clazz);
        }
    }

    private HashMap<String, Object> serializeMapSerializer(VivariumObject object) throws IllegalAccessException
    {
        HashMap<String, Object> map = new HashMap<>();
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

    private Set<Field> getSerializedParameters(VivariumObject object)
    {
        HashSet<Field> annotatedFields = new HashSet<>();
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

    public void deserialize(VivariumObject object, Map<String, Object> map)
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
                try
                {
                    Array.set(array, i, deserializedElement);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("size: " + size);
                    System.out.println("array: " + array);
                    System.out.println("deserializedElement: " + deserializedElement);
                    System.out.println("clazz: " + clazz);
                    System.out.println("clazz.getComponentType(): " + clazz.getComponentType());
                    System.out.println("element: " + element);
                }
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
        else if (VivariumObject.class.isAssignableFrom(clazz))
        {
            // Reference crap here
            return getReferenceObject(UUID.fromString((String) object));
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
        else if (clazz == UUID.class)
        {
            return UUID.fromString((String) object);
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
        else if (clazz == UUID.class)
        {
            return UUID.fromString(s);
        }
        else
        {
            throw new UnsupportedOperationException("Unable to parse primitive " + clazz);
        }
    }

    /**
     * Creates a deep copy of a vivarium object
     *
     * @param original
     *            The object to copy
     * @return The copy of the original object
     */
    @SuppressWarnings("unchecked")
    public <T extends VivariumObject> T makeCopy(T original)
    {
        MapCollection collection = serialize(original);
        VivariumObjectCollection collectionCopy = deserializeCollection(collection);
        return (T) collectionCopy.getObject(original.getUUID());
    }

    /**
     * Returns a List of MapSerializer objects from a serialized collection. The objects returned will be the objects
     * with the highest relative serialization category ranking.
     *
     * @param collection
     * @return
     */
    public VivariumObjectCollection deserializeCollection(MapCollection collection)
    {
        _collection = collection;
        VivariumObjectCollection objects = new VivariumObjectCollection();
        for (HashMap<String, Object> map : collection)
        {
            preDeserializeMap(map);
        }
        for (HashMap<String, Object> map : collection)
        {
            VivariumObject object = deserializeMap(map);
            objects.add(object);
        }
        return objects;
    }

}
