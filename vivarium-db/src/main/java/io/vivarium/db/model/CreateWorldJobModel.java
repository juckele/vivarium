package io.vivarium.db.model;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import io.vivarium.util.UUID;

public class CreateWorldJobModel extends JobModel
{
    public CreateWorldJobModel(UUID jobID, JobStatus status, short priority, UUID checkoutOutByWorkerID,
            Date checkoutOutTime, Date completedTime, Collection<UUID> inputResources, Collection<UUID> outputResources,
            Collection<UUID> jobDependencies)
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
