package io.vivarium.serialization;

import java.util.HashMap;
import java.util.Map;

public class ClassRegistry
{
    private static ClassRegistry _instance = new ClassRegistry();

    private Map<String, Class<? extends VivariumObject>> _map = new HashMap<>();

    public static ClassRegistry getInstance()
    {
        return _instance;
    };

    private ClassRegistry()
    {
    };

    public void register(Class<? extends VivariumObject> clazz)
    {
        _map.put(clazz.getSimpleName(), clazz);
    }

    public Class<? extends VivariumObject> getClassNamed(String name)
    {
        if (_map.containsKey(name))
        {
            return _map.get(name);
        }
        else
        {
            throw new IllegalStateException("Class " + name + " has not been registered in the class registry");
        }
    }
}