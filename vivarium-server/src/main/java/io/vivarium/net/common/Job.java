/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
