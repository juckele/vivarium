/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class RequestResourceMessage extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    final private UUID _resourceID;
    final private ResourceFormat _resourceFormat;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private RequestResourceMessage()
    {
        _resourceID = null;
        _resourceFormat = null;
    }

    @JsonCreator
    public RequestResourceMessage(@JsonProperty("resourceID") UUID resourceID,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat)
    {
        this._resourceID = resourceID;
        this._resourceFormat = resourceFormat;
    }

    @JsonSerialize(using = UUIDSerializer.class)
    public UUID getResourceID()
    {
        return _resourceID;
    }

    public ResourceFormat getResourceFormat()
    {
        return _resourceFormat;
    }
}
