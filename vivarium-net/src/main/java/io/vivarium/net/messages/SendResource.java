package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class SendResource extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    final public UUID resourceID;
    final public String dataString;
    final public ResourceFormat resourceFormat;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private SendResource()
    {
        this.resourceID = null;
        this.resourceFormat = null;
        this.dataString = null;
    }

    public SendResource(UUID resourceID, String dataString, ResourceFormat resourceFormat)
    {
        this.resourceID = resourceID;
        this.resourceFormat = resourceFormat;
        this.dataString = dataString;
    }

    @JsonCreator
    public SendResource(@JsonProperty("resourceID") UUID resourceID,
            @JsonProperty("resourceFormat") ResourceFormat resourceFormat,
            @JsonProperty("dataString") String dataString)
    {
        this.resourceID = resourceID;
        this.resourceFormat = resourceFormat;
        this.dataString = dataString;
    }
}
