package io.vivarium.web;

import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializer;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;

import io.vivarium.util.Version;

public class VersionDeserializer extends JsonDeserializer<Version>
{

    private static final VersionDeserializer INSTANCE = new VersionDeserializer();

    public static VersionDeserializer getInstance()
    {
        return INSTANCE;
    }

    private VersionDeserializer()
    {
    }

    @Override
    public Version doDeserialize(JsonReader reader, JsonDeserializationContext ctx, JsonDeserializerParameters params)
    {
        return new Version(reader.nextString());
    }
}
