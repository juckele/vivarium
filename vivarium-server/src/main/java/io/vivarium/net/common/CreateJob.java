package io.vivarium.net.common;

public class CreateJob extends Message
{
    public final Job job;

    public CreateJob(Job job)
    {
        this.job = job;
    }
}
