package io.vivarium.net.common.messages;

import io.vivarium.net.common.jobs.Job;

public class CreateJob extends Message
{
    public final Job job;

    public CreateJob(Job job)
    {
        this.job = job;
    }
}
