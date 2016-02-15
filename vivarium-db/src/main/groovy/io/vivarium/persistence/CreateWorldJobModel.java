package io.vivarium.persistence;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import io.vivarium.util.UUID;

public class CreateWorldJobModel extends JobModel
{
    public CreateWorldJobModel(UUID jobID, JobStatus status, short priority, UUID checkoutOutByWorkerID,
            Timestamp checkoutOutTime, Timestamp completedTime, Collection<UUID> inputResources,
            Collection<UUID> outputResources, Collection<UUID> jobDependencies)
    {
        super(jobID, JobType.CREATE_WORLD, status, priority, checkoutOutByWorkerID, checkoutOutTime, completedTime,
                inputResources, outputResources, jobDependencies);
    }

    public CreateWorldJobModel(Map<String, Object> relation, Map<String, String> properties,
            Collection<UUID> inputResources, Collection<UUID> outputResources, Collection<UUID> jobDependencies)
    {
        super(relation, properties, inputResources, outputResources, jobDependencies);
    }
}
