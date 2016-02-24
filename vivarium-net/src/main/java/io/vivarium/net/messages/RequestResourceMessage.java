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
}
