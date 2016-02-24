package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Message
{
    final private UUID _messageID;

    @JsonCreator
    protected Message(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID)
    {
        this._messageID = messageID;
    }

    @JsonSerialize(using = UUIDSerializer.class)
    public UUID getMessageID()
    {
        return _messageID;
    }
}
