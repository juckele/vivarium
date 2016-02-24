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
        _data = new LinkedList<>();
    }

    public void add(VivariumObject object)
    {
        _data.add(object);
    }

    @SuppressWarnings("unchecked") // Not actually unchecked...
    public <T extends VivariumObject> List<T> getAll(Class<T> clazz)
    {
        List<T> result = new LinkedList<>();

        for (VivariumObject object : _data)
        {
            Class<?> specificClazz = object.getClass();
            while (specificClazz != null)
            {
                if (specificClazz == clazz)
                {
                    result.add((T) object);
                    break;
                }
                specificClazz = specificClazz.getSuperclass();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked") // Not actually unchecked...
    public <T extends VivariumObject> T getFirst(Class<T> clazz)
    {
        for (VivariumObject object : _data)
        {
            Class<?> specificClazz = object.getClass();
            while (specificClazz != null)
            {
                if (specificClazz == clazz)
                {
                    return (T) object;
                }
                specificClazz = specificClazz.getSuperclass();
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
