/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import io.vivarium.persistence.PersistenceModule;

public class WorkloadEnforcer implements VoidFunction
{
    // static configs
    public final static long DEFAULT_ENFORCE_TIME_GAP_IN_MS = 60_000;

    // Dependencies
    private final PersistenceModule _persistenceModule;
    private final ClientConnectionManager _clientConnectionManager;

    public WorkloadEnforcer(PersistenceModule persistenceModule, ClientConnectionManager clientConnectionManager)
    {
        _persistenceModule = persistenceModule;
        _clientConnectionManager = clientConnectionManager;
    }

    @Override
    public void execute()
    {
        // Update the job statuses
        _persistenceModule.updateJobStatuses();

        // Get the current worker models

        // Get the top priority unblocked jobs, the currently assigned jobs
        // TODO: IMPLEMENT

        // Determine optimal greedy allocation of jobs
        // TODO: IMPLEMENT

        // Build plan to assign jobs if doing so can improve greedy allocation score
        // TODO: IMPLEMENT

        // If reassigning jobs was not able to improve greedy allocation score, check to see if a single unassignment +
        // reassignment can improve the score. This will fail to find complex rearrangements which might exist, but it
        // is assumed that these would be marginal improvements at best.
        // TODO: IMPLEMENT

        // Send assignments and await acks from workers for assignments.
        // TODO: IMPLEMENT
    }

}
