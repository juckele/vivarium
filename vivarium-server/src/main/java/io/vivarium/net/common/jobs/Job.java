/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Job
{
    public final JobType type;
    public final UUID jobID;
    public final List<Job> dependencies;

    public Job(List<Job> dependencies)
    {
        jobID = UUID.randomUUID();
        type = getType();
        this.dependencies = new ArrayList<Job>(dependencies);
    }

    protected abstract JobType getType();
}
