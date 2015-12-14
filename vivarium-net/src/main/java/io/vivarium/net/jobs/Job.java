/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Job
{
    public JobType type;
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
    public UUID jobID;
    @JsonSerialize(contentUsing = UUIDSerializer.class)
    @JsonDeserialize(contentUsing = UUIDDeserializer.class)
    public Collection<UUID> inputResources;
    @JsonSerialize(contentUsing = UUIDSerializer.class)
    @JsonDeserialize(contentUsing = UUIDDeserializer.class)
    public Collection<UUID> outputResources;
    @JsonSerialize(contentUsing = UUIDSerializer.class)
    @JsonDeserialize(contentUsing = UUIDDeserializer.class)
    public Collection<UUID> dependencies;

    protected Job()
    {
    }

    public Job(Collection<UUID> inputResources, Collection<UUID> outputResources, Collection<UUID> dependencies)
    {
        jobID = UUID.randomUUID();
        type = getType();
        this.inputResources = new ArrayList<UUID>(inputResources);
        this.outputResources = new ArrayList<UUID>(outputResources);
        this.dependencies = new ArrayList<UUID>(dependencies);
    }

    protected abstract JobType getType();
}
