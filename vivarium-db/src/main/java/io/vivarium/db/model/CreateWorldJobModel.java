package io.vivarium.db.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.vivarium.util.UUID;

public class CreateWorldJobModel extends PipeJobModel
{
    public CreateWorldJobModel(UUID jobID, JobStatus status, short priority, UUID checkoutOutByWorkerID,
            Date checkoutOutTime, Date completedTime, UUID sourceResourceID, UUID outputResourceID,
            List<UUID> jobDependencies)
    {
        super(jobID, JobType.CREATE_WORLD, status, priority, checkoutOutByWorkerID, checkoutOutTime, completedTime,
                sourceResourceID, outputResourceID, jobDependencies);
    }

    public CreateWorldJobModel(Map<String, Object> relation, List<UUID> jobDependencies)
    {
        super(relation, jobDependencies);
    }

    @Override
    public String getTableName()
    {
        return "create_world_jobs";
    }
}
