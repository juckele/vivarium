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
    public UUID resourceID;
    public String dataString;
    public ResourceFormat resourceFormat;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private SendResourceMessage()
    {
        this.resourceID = null;
        this.resourceFormat = null;
        this.dataString = null;
    }

    public SendResourceMessage(UUID resourceID, String dataString, ResourceFormat resourceFormat)
    {
        this.resourceID = resourceID;
        this.resourceFormat = resourceFormat;
        this.dataString = dataString;
    }

    @JsonCreator
    public SendResourceMessage(@JsonProperty("resourceID") UUID resourceID,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat,
            @JsonProperty("dataString") String dataString)
    {
        this.resourceID = resourceID;
        this.resourceFormat = resourceFormat;
        this.dataString = dataString;
    }
}
