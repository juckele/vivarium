package com.johnuckele.vivarium.serialization;

import java.util.Map;
import java.util.Set;

public interface MapSerializer
{
    Set<MapSerializer> getReferences();

    Map<String, String> finalizeSerialization(Map<String, String> map, Map<MapSerializer, Integer> referenceMap);

    void finalizeDeserialization(Map<String, String> map, Map<Integer, MapSerializer> dereferenceMap);
}
