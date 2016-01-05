/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class CreateWorldJob extends Job
{
    @JsonCreator
    public CreateWorldJob(@JsonProperty("type") JobType jobType,
            @JsonProperty("jobID") @JsonSerialize(using = UUIDSerializer.class) UUID jobID,
            @JsonProperty("messageID") Collection<UUID> inputResources,
            @JsonProperty("messageID") Collection<UUID> outputResources,
            @JsonProperty("messageID") Collection<UUID> dependencies)
    {
        super(jobType, jobID, inputResources, outputResources, dependencies);
        Preconditions.checkArgument(jobType == JobType.CREATE_WORLD);
    }

    public CreateWorldJob(Collection<UUID> inputResources, Collection<UUID> outputResources,
            Collection<UUID> dependencies)
    {
        this(JobType.CREATE_WORLD, UUID.randomUUID(), inputResources, outputResources, dependencies);
    }
}