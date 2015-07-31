package com.johnuckele.vivarium.serialization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SerializedCollection
{
    private Map<String, LinkedList<HashMap<String, String>>> _data;

    public SerializedCollection()
    {
        _data = new HashMap<String, LinkedList<HashMap<String, String>>>();
    }

    public void addObject(HashMap<String, String> obj)
    {
        String type = obj.get(SerializationEngine.CATEGORY_KEY);
        LinkedList<HashMap<String, String>> list;
        if (_data.containsKey(type))
        {
            list = _data.get(type);
        }
        else
        {
            list = new LinkedList<HashMap<String, String>>();
            _data.put(type, list);
        }
        list.add(obj);
    }

    public int categoryCount(SerializationCategory type)
    {
        if (_data.containsKey(type.name()))
        {
            LinkedList<HashMap<String, String>> list = _data.get(type.name());
            return list.size();
        }
        else
        {
            return 0;
        }
    }

    public boolean hasNext(SerializationCategory type)
    {
        if (_data.containsKey(type.name()))
        {
            return !_data.get(type.name()).isEmpty();
        }
        else
        {
            return false;
        }
    }

    public HashMap<String, String> popNext(SerializationCategory type)
    {
        LinkedList<HashMap<String, String>> list = _data.get(type.name());
        return list.pop();
    }

    @Override
    public String toString()
    {
        return _data.toString();
    }
}
