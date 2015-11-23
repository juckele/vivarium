/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.vivarium.util.Version;

public class VersionDeserializer extends JsonDeserializer<Version>
{

    @Override
    public Version deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {
        return new Version(p.getText());
    }
}