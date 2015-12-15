package io.vivarium.persistence.model;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;

public class RunSimulationJobModel extends JobModel
{
    // Column names
    protected static final String END_TICK_PROPERTY = "end_tick";
    private final Long endTick;

    public RunSimulationJobModel(UUID jobID, JobStatus status, short priority, UUID checkoutOutByWorkerID,
            Date checkoutOutTime, Date completedTime, long endTick, Collection<UUID> inputResources,
            Collection<UUID> outputResources, Collection<UUID> jobDependencies)
    {
        super(jobID, JobType.RUN_SIMULATION, status, priority, checkoutOutByWorkerID, checkoutOutTime, completedTime,
                inputResources, outputResources, jobDependencies);
        this.endTick = endTick;
    }

    public RunSimulationJobModel(Map<String, Object> relation, Map<String, String> properties,
            Collection<UUID> inputResources, Collection<UUID> outputResources, Collection<UUID> jobDependencies)
    {
        super(relation, properties, inputResources, outputResources, jobDependencies);
        Preconditions.checkNotNull(properties.get(END_TICK_PROPERTY));
        this.endTick = Long.parseLong(properties.get(END_TICK_PROPERTY));
    }

    @Override
    protected Map<String, String> buildProperties()
    {
        Map<String, String> properties = super.buildProperties();
        properties.put(END_TICK_PROPERTY, endTick.toString());
        return properties;
    }
}
