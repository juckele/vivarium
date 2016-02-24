package io.vivarium.serialization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class MapCollection implements Iterable<HashMap<String, Object>>
{
    private LinkedList<HashMap<String, Object>> _data;

    public MapCollection()
    {
        _data = new LinkedList<>();
    }

    public void addObject(HashMap<String, Object> obj)
    {
        _data.add(obj);
    }

    public boolean hasNext()
    {
        return _data.size() > 0;
    }

    public HashMap<String, Object> popNext()
    {
        return _data.removeLast();
    }

    @Override
    public String toString()
    {
        return _data.toString();
    }

    @Override
    public Iterator<HashMap<String, Object>> iterator()
    {
        return _data.iterator();
    }
}
