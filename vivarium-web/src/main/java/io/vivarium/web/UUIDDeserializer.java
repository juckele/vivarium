package io.vivarium.web;

import com.github.nmorel.gwtjackson.client.JsonDeserializationContext;
import com.github.nmorel.gwtjackson.client.JsonDeserializer;
import com.github.nmorel.gwtjackson.client.JsonDeserializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonReader;

import io.vivarium.util.UUID;

public class UUIDDeserializer extends JsonDeserializer<UUID>
{

    private static final UUIDDeserializer INSTANCE = new UUIDDeserializer();

    public static UUIDDeserializer getInstance()
    {
        return INSTANCE;
    }

    private UUIDDeserializer()
    {
    }

    @Override
    public UUID doDeserialize(JsonReader reader, JsonDeserializationContext ctx, JsonDeserializerParameters params)
    {
        return UUID.fromString(reader.nextString());
    }
}
