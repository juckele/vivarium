/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.List;

import io.vivarium.util.UUID;

public class CreateWorldJob extends PipeJob
{
    public CreateWorldJob(List<UUID> dependencies, UUID sourceDocumentID, UUID outputDocumentID)
    {
        super(dependencies, sourceDocumentID, outputDocumentID);
    }

    @Override
    protected JobType getType()
    {
        return JobType.CREATE_WORLD;
    }
}