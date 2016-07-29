package io.vivarium.client;

import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.serialization.VivariumObject;
import io.vivarium.serialization.VivariumObjectCopier;

public class StreamingObjectCopier implements VivariumObjectCopier
{
    @SuppressWarnings("unchecked")
    @Override
    public <T extends VivariumObject> T copyObject(T object)
    {
        String streamString = Streamer.get().toString(object);
        return (T) Streamer.get().fromString(streamString);
    }

}