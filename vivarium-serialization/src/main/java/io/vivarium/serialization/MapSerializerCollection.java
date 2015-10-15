package io.vivarium.serialization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapSerializerCollection
{
    private Map<SerializationCategory, List<VivariumObject>> _data;

    public MapSerializerCollection()
    {
        _data = new HashMap<SerializationCategory, List<VivariumObject>>();
    }

    public void add(VivariumObject object)
    {
        // Get the list of existing objects of this class
        SerializationCategory category = SerializationCategory.getCategoryForClass(object.getClass());
        List<VivariumObject> list = _data.get(category);

        // Create the list if it's missing
        if (list == null)
        {
            list = new LinkedList<VivariumObject>();
            _data.put(category, list);
        }

        // Add this object to the collection
        list.add(object);
    }

    public List<VivariumObject> get(SerializationCategory category)
    {
        // Get the existing list
        List<VivariumObject> list = _data.get(category);

        // Create the list if it's missing
        if (list == null)
        {
            list = new LinkedList<VivariumObject>();
            _data.put(category, list);
        }

        // Return the list
        return list;
    }
}
