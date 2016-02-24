package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.net.jobs.Job;
import io.vivarium.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class CreateJobMessage extends Message
{
    private final Job _job;

    public CreateJobMessage(Job job)
    {
        this(UUID.randomUUID(), job);
    }

    @JsonCreator
    public CreateJobMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID,
            @JsonProperty("job") Job job)
    {
        super(messageID);
        this._job = job;
    }

    public Job getJob()
    {
        return _job;
    }
}
