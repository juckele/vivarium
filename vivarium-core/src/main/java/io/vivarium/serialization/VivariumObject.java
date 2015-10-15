package io.vivarium.serialization;

import com.googlecode.gwtstreamer.client.Streamable;

import io.vivarium.util.UUID;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class VivariumObject implements Streamable
{
    @SerializedParameter
    UUID _uuid = UUID.randomUUID();

    public abstract void finalizeSerialization();
}
