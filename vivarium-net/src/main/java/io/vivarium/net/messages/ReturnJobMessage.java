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
public class ReturnJobMessage extends Message
{
    @JsonCreator
    protected ReturnJobMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID)
    {
        super(messageID);
    }
}
