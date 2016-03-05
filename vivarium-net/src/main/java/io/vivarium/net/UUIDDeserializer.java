package io.vivarium.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.vivarium.util.UUID;

public class UUIDDeserializer extends JsonDeserializer<UUID>
{

    @Override
    public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        return UUID.fromString(p.getText());
    }
}