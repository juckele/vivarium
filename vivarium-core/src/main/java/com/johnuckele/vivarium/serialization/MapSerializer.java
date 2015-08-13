package com.johnuckele.vivarium.serialization;

import java.util.List;

public interface MapSerializer
{
    List<MapSerializer> getReferences();

    List<SerializedParameter> getMappedParameters();

    Object getValue(String key);

    void setValue(String key, Object value);

    SerializationCategory getSerializationCategory();
}
