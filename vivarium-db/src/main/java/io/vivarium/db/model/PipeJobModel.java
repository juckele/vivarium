package io.vivarium.db.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;

public abstract class PipeJobModel extends JobModel
{
    // Column names
    protected static final String SOURCE_RESOURCE_ID = "source_resource_id";
    protected static final String OUTPUT_RESOURCE_ID = "output_resource_id";
    private final UUID sourceResourceID;
    private final UUID outputResourceID;

    public PipeJobModel(UUID jobID, JobType type, JobStatus status, short priority, UUID checkoutOutByWorkerID,
            Date checkoutOutTime, Date completedTime, UUID sourceResourceID, UUID outputResourceID,
            List<UUID> jobDependencies)
    {
        super(jobID, type, status, priority, checkoutOutByWorkerID, checkoutOutTime, completedTime, jobDependencies);
        Preconditions.checkNotNull(sourceResourceID, "sourceResourceID cannot be null");
        Preconditions.checkNotNull(outputResourceID, "outputResourceID cannot be null");
        this.sourceResourceID = sourceResourceID;
        this.outputResourceID = outputResourceID;
    }

    public PipeJobModel(Map<String, Object> relation, List<UUID> jobDependencies)
    {
        super(relation, jobDependencies);
        Preconditions.checkNotNull(relation.get(SOURCE_RESOURCE_ID), "sourceResourceID cannot be null");
        Preconditions.checkNotNull(relation.get(OUTPUT_RESOURCE_ID), "outputResourceID cannot be null");
        this.sourceResourceID = UUID.fromString(relation.get(SOURCE_RESOURCE_ID).toString());
        this.outputResourceID = UUID.fromString(relation.get(OUTPUT_RESOURCE_ID).toString());
    }

    @Override
    protected Map<String, Object> getRelationModel()
    {
        Map<String, Object> relation = super.getRelationModel();
        relation.put(SOURCE_RESOURCE_ID, sourceResourceID);
        relation.put(OUTPUT_RESOURCE_ID, outputResourceID);
        return relation;
    }
}
