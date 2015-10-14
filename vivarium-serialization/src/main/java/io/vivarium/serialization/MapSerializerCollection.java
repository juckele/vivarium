package io.vivarium.serialization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapSerializerCollection
{
    private Map<SerializationCategory, List<MapSerializer>> _data;

    public MapSerializerCollection()
    {
        _data = new HashMap<SerializationCategory, List<MapSerializer>>();
    }

    public void add(MapSerializer object)
    {
        // Get the list of existing objects of this class
        SerializationCategory category = SerializationCategory.getCategoryForClass(object.getClass());
        List<MapSerializer> list = _data.get(category);

        // Create the list if it's missing
        if (list == null)
        {
            list = new LinkedList<MapSerializer>();
            _data.put(category, list);
        }

        // Add this object to the collection
        list.add(object);
    }

    public List<MapSerializer> get(SerializationCategory category)
    {
        // Get the existing list
        List<MapSerializer> list = _data.get(category);

        // Create the list if it's missing
        if (list == null)
        {
            list = new LinkedList<MapSerializer>();
            _data.put(category, list);
        }

        // Return the list
        return list;
    }
}
