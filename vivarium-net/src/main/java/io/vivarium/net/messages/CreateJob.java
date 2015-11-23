/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vivarium.net.jobs.Job;

public class CreateJob extends Message
{
    public final Job job;

    @JsonCreator
    public CreateJob(@JsonProperty("job") Job job)
    {
        this.job = job;
    }
}
