package com.johnuckele.vivarium.serialization;

import java.util.HashMap;
import java.util.LinkedList;

public class SerializedCollection
{
    private HashMap<String, LinkedList<HashMap<String, String>>> _data;

    public SerializedCollection()
    {
        _data = new HashMap<String, LinkedList<HashMap<String, String>>>();
    }

    public void addObject(String type, HashMap<String, String> obj)
    {
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

    public boolean hasNext(String type)
    {
        if (_data.containsKey(type))
        {
            return !_data.get(type).isEmpty();
        }
        else
        {
            return false;
        }
    }

    public HashMap<String, String> popNext(String type)
    {
        LinkedList<HashMap<String, String>> list = _data.get(type);
        return list.pop();
    }

    @Override
    public String toString()
    {
        return _data.toString();
    }
}
