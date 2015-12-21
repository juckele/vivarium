package io.vivarium.server.workloadmanagement;

import io.vivarium.util.UUID;

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

    @Override
    public String toString()
    {
        return "JobAssignmentOperation [_action=" + _action + ", _workerID=" + _workerID + ", _jobID=" + _jobID + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_action == null) ? 0 : _action.hashCode());
        result = prime * result + ((_jobID == null) ? 0 : _jobID.hashCode());
        result = prime * result + ((_workerID == null) ? 0 : _workerID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        JobAssignmentOperation other = (JobAssignmentOperation) obj;
        if (_action != other._action)
        {
            return false;
        }
        if (_jobID == null)
        {
            if (other._jobID != null)
            {
                return false;
            }
        }
        else if (!_jobID.equals(other._jobID))
        {
            return false;
        }
        if (_workerID == null)
        {
            if (other._workerID != null)
            {
                return false;
            }
        }
        else if (!_workerID.equals(other._workerID))
        {
            return false;
        }
        return true;
    }

}
