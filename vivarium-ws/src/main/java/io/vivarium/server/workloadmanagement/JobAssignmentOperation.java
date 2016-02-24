package io.vivarium.server.workloadmanagement;

import io.vivarium.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JobAssignmentOperation
{
    private final JobAssignmentAction _action;
    private final UUID _workerID;
    private final UUID _jobID;

    public JobAssignmentOperation(JobAssignmentAction action, UUID workerID, UUID jobID)
    {
        super();
        this._action = action;
        this._workerID = workerID;
        this._jobID = jobID;
    }

    public JobAssignmentAction getAction()
    {
        return _action;
    }

    public UUID getWorkerID()
    {
        return _workerID;
    }

    public UUID getJobID()
    {
        return _jobID;
    }
}
