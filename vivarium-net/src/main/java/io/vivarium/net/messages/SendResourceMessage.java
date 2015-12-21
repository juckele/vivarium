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

public class SendResourceMessage extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    final private UUID _resourceID;
    final private String _dataString;
    final private ResourceFormat _resourceFormat;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private SendResourceMessage()
    {
        this._resourceID = null;
        this._resourceFormat = null;
        this._dataString = null;
    }

    public SendResourceMessage(UUID resourceID, String dataString, ResourceFormat resourceFormat)
    {
        this._resourceID = resourceID;
        this._resourceFormat = resourceFormat;
        this._dataString = dataString;
    }

    @JsonCreator
    public SendResourceMessage(@JsonProperty("resourceID") UUID resourceID,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat,
            @JsonProperty("dataString") String dataString)
    {
        this._resourceID = resourceID;
        this._resourceFormat = resourceFormat;
        this._dataString = dataString;
    }

    public UUID getResourceID()
    {
        return _resourceID;
    }

    public String getDataString()
    {
        return _dataString;
    }

    public ResourceFormat getResourceFormat()
    {
        return _resourceFormat;
    }
}
