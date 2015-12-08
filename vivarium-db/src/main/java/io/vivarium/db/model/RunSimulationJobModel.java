package io.vivarium.db.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;

public class RunSimulationJobModel extends PipeJobModel
{
    // Column names
    protected static final String END_TICK = "end_tick";
    private final Long endTick;

    public RunSimulationJobModel(UUID jobID, JobStatus status, short priority, UUID checkoutOutByWorkerID,
            Date checkoutOutTime, Date completedTime, long endTick, UUID sourceResourceID, UUID outputResourceID,
            List<UUID> jobDependencies)
    {
        super(jobID, JobType.RUN_SIMULATION, status, priority, checkoutOutByWorkerID, checkoutOutTime, completedTime,
                sourceResourceID, outputResourceID, jobDependencies);
        this.endTick = endTick;
    }

    public RunSimulationJobModel(Map<String, Object> relation, List<UUID> jobDependencies)
    {
        super(relation, jobDependencies);
        Preconditions.checkNotNull(relation.get(END_TICK), "endTick cannot be null");
        this.endTick = (Long) relation.get(END_TICK);
    }

    @Override
    protected Map<String, Object> getRelationModel()
    {
        Map<String, Object> relation = super.getRelationModel();
        relation.put(END_TICK, endTick);
        return relation;
    }

    @Override
    public String getTableName()
    {
        return "run_simulation_jobs";
    }
}
