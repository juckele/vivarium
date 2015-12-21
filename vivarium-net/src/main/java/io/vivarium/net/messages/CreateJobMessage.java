/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vivarium.net.jobs.Job;

public class CreateJobMessage extends Message
{
    private final Job _job;

    @JsonCreator
    public CreateJobMessage(@JsonProperty("job") Job job)
    {
        this._job = job;
    }

    public Job getJob()
    {
        return _job;
    }
}
