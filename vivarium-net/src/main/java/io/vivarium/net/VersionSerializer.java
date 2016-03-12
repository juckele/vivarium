package io.vivarium.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import io.vivarium.util.Version;

public class VersionSerializer extends JsonSerializer<Version>
{
    @Override
    public void serialize(Version value, JsonGenerator generator, SerializerProvider serializers) throws IOException
    {
        generator.writeString(value.toString());
    }
}