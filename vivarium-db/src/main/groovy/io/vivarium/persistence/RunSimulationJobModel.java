package io.vivarium.persistence;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;

public class RunSimulationJobModel extends JobModel
{
    // Column names
    protected static final String END_TICK_PROPERTY = "end_tick";
    private final Long endTick;

    public RunSimulationJobModel(UUID jobID, JobStatus status, int priority, UUID checkoutOutByWorkerID,
            Timestamp checkoutOutTime, Timestamp completedTime, long endTick, Collection<UUID> inputResources,
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((endTick == null) ? 0 : endTick.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        RunSimulationJobModel other = (RunSimulationJobModel) obj;
        if (endTick == null)
        {
            if (other.endTick != null)
            {
                return false;
            }
        }
        else if (!endTick.equals(other.endTick))
        {
            return false;
        }
        return true;
    }
}
