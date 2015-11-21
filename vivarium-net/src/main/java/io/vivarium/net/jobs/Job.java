/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Job
{
    public final JobType type;
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public final UUID jobID;
    public final List<UUID> dependencies;

    public Job(List<UUID> dependencies)
    {
        jobID = UUID.randomUUID();
        type = getType();
        this.dependencies = new ArrayList<UUID>(dependencies);
    }

    protected abstract JobType getType();
}
