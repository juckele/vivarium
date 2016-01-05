/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.net.jobs.Job;
import io.vivarium.util.UUID;

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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_job == null) ? 0 : _job.hashCode());
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
        CreateJobMessage other = (CreateJobMessage) obj;
        if (_job == null)
        {
            if (other._job != null)
            {
                return false;
            }
        }
        else if (!_job.equals(other._job))
        {
            return false;
        }
        return true;
    }
}
