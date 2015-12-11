/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.List;

import io.vivarium.util.UUID;

public class SimulationJob extends PipeJob
{
    public long endTick;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private SimulationJob()
    {
        super();
        this.endTick = 0;
    }

    public SimulationJob(List<UUID> dependencies, UUID sourceDocumentID, UUID outputDocumentID, long endTick)
    {
        super(dependencies, sourceDocumentID, outputDocumentID);
        this.endTick = endTick;
    }

    @Override
    protected JobType getType()
    {
        return JobType.RUN_SIMULATION;
    }
}
