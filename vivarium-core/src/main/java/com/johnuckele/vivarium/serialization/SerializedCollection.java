package com.johnuckele.vivarium.serialization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SerializedCollection
{
    private Map<String, LinkedList<HashMap<String, Object>>> _data;

    public SerializedCollection()
    {
        _data = new HashMap<String, LinkedList<HashMap<String, Object>>>();
    }

    public void addObject(HashMap<String, Object> obj)
    {
        String type = (String) obj.get(SerializationEngine.CATEGORY_KEY);
        LinkedList<HashMap<String, Object>> list;
        if (_data.containsKey(type))
        {
            list = _data.get(type);
        }
        else
        {
            list = new LinkedList<HashMap<String, Object>>();
            _data.put(type, list);
        }
        list.add(obj);
    }

    public int categoryCount(SerializationCategory type)
    {
        if (_data.containsKey(type.name()))
        {
            LinkedList<HashMap<String, Object>> list = _data.get(type.name());
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

    public HashMap<String, Object> popNext(SerializationCategory type)
    {
        LinkedList<HashMap<String, Object>> list = _data.get(type.name());
        return list.pop();
    }

    @Override
    public String toString()
    {
        return _data.toString();
    }
}
