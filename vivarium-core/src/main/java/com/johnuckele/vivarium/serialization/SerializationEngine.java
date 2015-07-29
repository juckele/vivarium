package com.johnuckele.vivarium.serialization;

import java.util.HashMap;

import com.johnuckele.vivarium.core.Species;

public abstract class SerializationEngine
{
    private SerializationWorker _worker;

    public SerializationEngine(SerializationWorker worker)
    {
        _worker = worker;
    }

    public HashMap<String, String> SerializeSpecies(Species s)
    {
        return s.serialize();
    }

    public String getKeyFromFieldName(String fieldName)
    {
        return fieldName.substring(fieldName.lastIndexOf('_') + 1);
    }
}
