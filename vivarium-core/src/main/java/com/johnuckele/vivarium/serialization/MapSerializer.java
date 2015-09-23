package com.johnuckele.vivarium.serialization;

import com.googlecode.gwtstreamer.client.Streamable;

public interface MapSerializer extends Streamable
{
    public void finalizeSerialization();
}
