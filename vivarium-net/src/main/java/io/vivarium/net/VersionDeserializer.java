package io.vivarium.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.vivarium.util.Version;

public class VersionDeserializer extends JsonDeserializer<Version>
{

    @Override
    public Version deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        return new Version(p.getText());
    }
}