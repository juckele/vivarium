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

    public RequestResourceMessage(UUID resourceID, ResourceFormat resourceFormat)
    {
        this(UUID.randomUUID(), resourceID, resourceFormat);
    }

    @JsonCreator
    public RequestResourceMessage(
            @JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID,
            @JsonProperty("resourceID") @JsonSerialize(using = UUIDSerializer.class) UUID resourceID,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat)
    {
        super(messageID);
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_resourceFormat == null) ? 0 : _resourceFormat.hashCode());
        result = prime * result + ((_resourceID == null) ? 0 : _resourceID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        RequestResourceMessage other = (RequestResourceMessage) obj;
        if (_resourceFormat != other._resourceFormat)
        {
            return false;
        }
        if (_resourceID == null)
        {
            if (other._resourceID != null)
            {
                return false;
            }
        }
        else if (!_resourceID.equals(other._resourceID))
        {
            return false;
        }
        return true;
    }
}
