package io.vivarium.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import io.vivarium.util.UUID;

public class UUIDSerializer extends JsonSerializer<UUID>
{
    @Override
    public void serialize(UUID value, JsonGenerator generator, SerializerProvider serializers) throws IOException
    {
        generator.writeString(value.toString());
    }
}
