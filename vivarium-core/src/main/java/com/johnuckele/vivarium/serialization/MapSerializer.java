package com.johnuckele.vivarium.serialization;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MapSerializer
{
    Set<MapSerializer> getReferences();

    Map<String, String> finalizeSerialization(Map<String, String> map, Map<MapSerializer, Integer> referenceMap);

    void finalizeDeserialization(Map<String, String> map, Map<Integer, MapSerializer> dereferenceMap);

    List<SerializedParameter> getMappedParameters();

    Object getValue(String key);

    void setValue(String key, Object value);

    SerializationCategory getSerializationCategory();
}
