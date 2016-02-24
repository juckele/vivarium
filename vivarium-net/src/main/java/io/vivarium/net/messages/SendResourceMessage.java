package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
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
}
