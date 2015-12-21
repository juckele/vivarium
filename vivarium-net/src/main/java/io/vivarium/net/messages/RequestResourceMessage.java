/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class RequestResourceMessage extends Message
{
    final private UUID _resourceID;
    final private ResourceFormat _resourceFormat;

    @JsonCreator
    public RequestResourceMessage(
            @JsonProperty("resourceID") @JsonSerialize(using = UUIDSerializer.class) UUID resourceID,
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
