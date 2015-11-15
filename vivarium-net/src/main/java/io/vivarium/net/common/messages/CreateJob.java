package io.vivarium.net.common.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vivarium.net.common.jobs.Job;

public class CreateJob extends Message
{
    public final Job job;

    @JsonCreator
    public CreateJob(@JsonProperty("job") Job job)
    {
        this.job = job;
    }
}
