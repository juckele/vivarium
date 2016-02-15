package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class RequestJobMessage extends Message
{
    @JsonCreator
    protected RequestJobMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID)
    {
        super(messageID);
    }
}
