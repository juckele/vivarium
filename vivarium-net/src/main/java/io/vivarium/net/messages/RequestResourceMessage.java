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
    final public UUID resourceID;
    final public ResourceFormat resourceFormat;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private RequestResourceMessage()
    {
        resourceID = null;
        resourceFormat = null;
    }

    @JsonCreator
    public RequestResourceMessage(@JsonProperty("resourceID") UUID resourceID,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat)
    {
        this.resourceID = resourceID;
        this.resourceFormat = resourceFormat;
    }
}
