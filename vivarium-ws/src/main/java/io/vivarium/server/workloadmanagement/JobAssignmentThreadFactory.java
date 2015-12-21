package io.vivarium.server.workloadmanagement;

import io.vivarium.server.ClientConnectionManager;

public class JobAssignmentThreadFactory
{
    private final ClientConnectionManager _clientConnectionManager;

    public JobAssignmentThreadFactory(ClientConnectionManager clientConnectionManager)
    {
        this._clientConnectionManager = clientConnectionManager;
    }

    public JobAssignmentThread make(JobAssignmentOperation jobAssingmentOperation)
    {
        return new JobAssignmentThread(_clientConnectionManager, jobAssingmentOperation);
    }
}
