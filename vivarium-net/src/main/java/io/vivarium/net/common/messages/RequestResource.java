package io.vivarium.net.common.messages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class RequestResource extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    final public UUID resourceID;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private RequestResource()
    {
        resourceID = null;
    }

    public RequestResource(UUID resourceID)
    {
        this.resourceID = resourceID;
    }
}
