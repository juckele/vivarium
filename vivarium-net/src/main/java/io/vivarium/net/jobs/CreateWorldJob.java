/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.Collection;

import io.vivarium.util.UUID;

public class CreateWorldJob extends Job
{
    @SuppressWarnings("unused") // Used for Jackson deserialization
    private CreateWorldJob()
    {
        super();
    }

    public CreateWorldJob(Collection<UUID> inputResources, Collection<UUID> outputResources,
            Collection<UUID> dependencies)
    {
        super(inputResources, outputResources, dependencies);
    }

    @Override
    protected JobType getType()
    {
        return JobType.CREATE_WORLD;
    }
}