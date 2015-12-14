/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.jobs;

import java.util.Collection;

import io.vivarium.util.UUID;

public class SimulationJob extends Job
{
    public long endTick;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private SimulationJob()
    {
        super();
        this.endTick = 0;
    }

    public SimulationJob(Collection<UUID> inputResources, Collection<UUID> outputResources,
            Collection<UUID> dependencies, long endTick)
    {
        super(inputResources, outputResources, dependencies);
        this.endTick = endTick;
    }

    @Override
    protected JobType getType()
    {
        return JobType.RUN_SIMULATION;
    }
}
