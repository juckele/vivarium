package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class SendResourceMessage extends Message
{
    final private UUID _resourceID;
    final private String _dataString;
    final private ResourceFormat _resourceFormat;

    public SendResourceMessage(UUID resourceID, String dataString, ResourceFormat resourceFormat)
    {
        this(UUID.randomUUID(), resourceID, dataString, resourceFormat);
    }

    @JsonCreator
    public SendResourceMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID,
            @JsonProperty("resourceID") @JsonSerialize(using = UUIDSerializer.class) UUID resourceID,
            @JsonProperty("dataString") String dataString,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat)
    {
        super(messageID);

        this._resourceID = resourceID;
        this._dataString = dataString;
        this._resourceFormat = resourceFormat;
    }

    @JsonSerialize(using = UUIDSerializer.class)
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_dataString == null) ? 0 : _dataString.hashCode());
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
        SendResourceMessage other = (SendResourceMessage) obj;
        if (_dataString == null)
        {
            if (other._dataString != null)
            {
                return false;
            }
        }
        else if (!_dataString.equals(other._dataString))
        {
            return false;
        }
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
