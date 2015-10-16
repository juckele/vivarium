package io.vivarium.serialization;

import java.util.LinkedList;
import java.util.List;

import com.googlecode.gwtstreamer.client.Streamable;

import io.vivarium.util.UUID;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class VivariumObjectCollection implements Streamable
{
    private List<VivariumObject> _data;

    public VivariumObjectCollection()
    {
        _data = new LinkedList<VivariumObject>();
    }

    public void add(VivariumObject object)
    {
        _data.add(object);
    }

    @SuppressWarnings("unchecked") // Not actually unchecked...
    public <T extends VivariumObject> List<T> get(Class<T> clazz)
    {
        List<T> result = new LinkedList<T>();

        for (VivariumObject object : _data)
        {
            if (clazz.isAssignableFrom(object.getClass()))
            {
                result.add((T) object);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked") // Not actually unchecked...
    public <T extends VivariumObject> T getFirst(Class<T> clazz)
    {
        for (VivariumObject object : _data)
        {
            if (clazz.isAssignableFrom(object.getClass()))
            {
                return (T) object;
            }
        }
        return null;
    }

    public VivariumObject getObject(UUID uuid)
    {
        for (VivariumObject object : _data)
        {
            if (object.getUUID().equals(uuid))
            {
                return object;
            }
        }
        return null;
    }
}
