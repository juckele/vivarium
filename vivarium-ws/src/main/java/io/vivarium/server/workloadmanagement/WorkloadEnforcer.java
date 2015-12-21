/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server.workloadmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import io.vivarium.persistence.JobModel;
import io.vivarium.persistence.JobStatus;
import io.vivarium.persistence.PersistenceModule;
import io.vivarium.persistence.WorkerModel;
import io.vivarium.server.ClientConnectionManager;
import io.vivarium.util.UUID;
import io.vivarium.util.concurrency.VoidFunction;

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
        List<WorkerModel> workers = _persistenceModule.fetchAllWorkers();
        // @formatter:off
        List<WorkerModel> activeWorkers = workers.stream()
                .filter(w -> w.isActive())
                .collect(Collectors.toList());
        // @formatter:on

        // Get the top priority unblocked jobs, the currently assigned jobs
        List<JobModel> waitingJobs = _persistenceModule.fetchJobsWithStatus(JobStatus.WAITING);
        List<JobModel> assignedJobs = _persistenceModule.fetchJobsWithStatus(JobStatus.PROCESSING);
        List<JobModel> prioritySortedJobs = new ArrayList<>(waitingJobs);
        prioritySortedJobs.addAll(assignedJobs);
        prioritySortedJobs.sort(new JobPriorityComparator());

        // Determine optimal greedy allocation of jobs and the current real allocation
        JobAssignments idealAssingments = buildDesiredJobAssingments(activeWorkers, prioritySortedJobs);
        JobAssignments actualAssingments = buildActualJobAssingments(activeWorkers, assignedJobs);

        // Build plan to assign jobs (only) if doing so can improve the allocation score
        // TODO: IMPLEMENT

        // If reassigning jobs was not able to improve greedy allocation score, check to see if a single unassignment +
        // reassignment can improve the score. This will fail to find complex rearrangements which might exist, but it
        // is assumed that these would be marginal improvements at best.
        // TODO: IMPLEMENT

        // Send assignments and await acks from workers for assignments.
        // TODO: IMPLEMENT
    }

    private JobAssignments buildDesiredJobAssingments(Collection<WorkerModel> workers,
            List<JobModel> prioritySortedJobs)
    {
        JobAssignments desiredAssingments = new JobAssignments(workers);
        Map<UUID, WorkerModel> workersByID = Maps.uniqueIndex(workers, WorkerModel::getWorkerID);
        for (JobModel job : prioritySortedJobs)
        {
            long maxScoreImprovement = 0;
            WorkerModel assignToWorker = null;
            for (WorkerModel worker : workers)
            {
                long workerScoreImprovment = desiredAssingments.getScoreChangeForJob(worker, job.getPriority());
                if (workerScoreImprovment > maxScoreImprovement)
                {
                    maxScoreImprovement = workerScoreImprovment;
                    assignToWorker = worker;
                }
            }
            if (assignToWorker != null)
            {
                desiredAssingments.addWorkerJob(assignToWorker, job.getPriority());
            }
        }
        return desiredAssingments;
    }

    private JobAssignments buildActualJobAssingments(Collection<WorkerModel> workers, List<JobModel> assignedJobs)
    {
        JobAssignments actualAssingments = new JobAssignments(workers);
        Map<UUID, WorkerModel> workersByID = Maps.uniqueIndex(workers, WorkerModel::getWorkerID);
        for (JobModel job : assignedJobs)
        {
            UUID workerID = job.getCheckedOutByWorkerID().get();
            actualAssingments.addWorkerJob(workersByID.get(workerID), job.getPriority());
        }
        return actualAssingments;
    }
}
