package io.vivarium.server.workloadmanagement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.google.common.collect.Maps;

import io.vivarium.persistence.JobModel;
import io.vivarium.persistence.JobStatus;
import io.vivarium.persistence.PersistenceModule;
import io.vivarium.persistence.WorkerModel;
import io.vivarium.util.UUID;
import io.vivarium.util.concurrency.VoidFunction;

public class WorkloadEnforcer implements VoidFunction
{
    // static configs
    public final static long DEFAULT_ENFORCE_TIME_GAP_IN_MS = 60_000;

    // Dependencies
    private final PersistenceModule _persistenceModule;
    private final JobAssignmentThreadFactory _jobAssignmentThreadFactory;

    private List<JobAssignmentThread> _jobAssignmentThreads;

    public WorkloadEnforcer(PersistenceModule persistenceModule, JobAssignmentThreadFactory jobAssignmentThreadFactory)
    {
        _persistenceModule = persistenceModule;
        _jobAssignmentThreadFactory = jobAssignmentThreadFactory;
    }

    @Override
    // @formatter:off (this method makes heavy use of lambdas, so auto-formatting is turned off for readability
    public void execute()
    {
        // Update the job statuses
        _persistenceModule.updateJobStatuses();

        // Get the current worker models
        List<WorkerModel> workers = _persistenceModule.fetchAllWorkers();
        List<WorkerModel> activeWorkers = workers.stream()
                .filter(w -> w.isActive())
                .collect(Collectors.toList());

        // Get the top priority unblocked jobs, the currently assigned jobs
        List<JobModel> waitingJobs = _persistenceModule.fetchJobsWithStatus(JobStatus.WAITING);
        List<JobModel> assignedJobs = _persistenceModule.fetchJobsWithStatus(JobStatus.PROCESSING);
        List<JobModel> prioritySortedJobs = ListUtils.union(waitingJobs, assignedJobs);
        prioritySortedJobs.sort(new JobPriorityComparator());

        // Determine optimal greedy allocation of jobs and the current real allocation
        JobAssignments idealAssingments = buildDesiredJobAssingments(activeWorkers, prioritySortedJobs);
        JobAssignments actualAssingments = buildActualJobAssingments(activeWorkers, assignedJobs);

        // Determine how many jobs of each priority need to give/take from each worker.
        JobAssignments jobsAssignmentsToGive = JobAssignments.subtract(idealAssingments, actualAssingments);
        JobAssignments jobsAssignmentsToTake = JobAssignments.subtract(actualAssingments, idealAssingments);

        // Determine which jobs will be taken back
        Map<Integer, List<JobModel>> assignedJobsByPriority = assignedJobs.stream()
                .collect(Collectors.groupingBy(JobModel::getPriority));
        List<JobAssignmentOperation> takeJobOperations = buildListOfJobsToTake(jobsAssignmentsToTake,
                assignedJobsByPriority);

        // Determine which jobs are available (or will be available) for assignment
        Set<UUID> jobIDsToTake = takeJobOperations.stream()
                .map(jobOperation -> jobOperation.getWorkerID())
                .collect(Collectors.toSet());
        List<JobModel> jobsToTake = assignedJobs.stream()
                .filter(job -> jobIDsToTake.contains(job.getJobID()))
                .collect(Collectors.toList());
        List<JobModel> availableJobs = ListUtils.union(waitingJobs, jobsToTake);
        Map<Integer, List<JobModel>> availableJobsByPriority = availableJobs.stream()
                .collect(Collectors.groupingBy(JobModel::getPriority));

        // Determine which jobs will be given out
        List<JobAssignmentOperation> giveJobOperations = buildListOfJobsToGive(jobsAssignmentsToGive,
                availableJobsByPriority);

        // Because we have to wait for worker acknowledgments before we can finish taking a job back that we want to
        // re-assign, we want to create a thread for every individual assign and take operation. Each assign operation
        // will have an job future. As soon as the job future is available, we can talk to that worker and give it the
        // job. Jobs which are already unassigned will get their future fulfilled immediately, but any jobs that the
        // network will need to wait for will only get fulfilled when the worker who currently has that job has returned
        // it.
        List<JobAssignmentOperation> allJobOperations = ListUtils.union(takeJobOperations, giveJobOperations);
        _jobAssignmentThreads = allJobOperations.stream()
                .map(jobOperation -> _jobAssignmentThreadFactory.make(jobOperation))
                .collect(Collectors.toList());
        _jobAssignmentThreads.stream().forEach(jobThread -> jobThread.start());
        _jobAssignmentThreads.stream().forEach(jobThread -> jobThread.join());
    }
    // @formatter:on

    private JobAssignments buildDesiredJobAssingments(Collection<WorkerModel> workers,
            List<JobModel> prioritySortedJobs)
    {
        JobAssignments desiredAssingments = new JobAssignments(workers);
        for (JobModel job : prioritySortedJobs)
        {
            long maxScoreImprovement = 0;
            WorkerModel assignToWorker = null;
            for (WorkerModel worker : workers)
            {
                if (desiredAssingments.isWorkerFull(worker))
                {
                    continue;
                }
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

    private List<JobAssignmentOperation> buildListOfJobsToTake(JobAssignments jobsAssignmentsToTake,
            Map<Integer, List<JobModel>> assignedJobsByPriority)
    {
        return convertJobAssignmentsIntoJobAssignmentOperations(JobAssignmentAction.TAKE, jobsAssignmentsToTake,
                assignedJobsByPriority);
    }

    private List<JobAssignmentOperation> buildListOfJobsToGive(JobAssignments jobsAssignmentsToGive,
            Map<Integer, List<JobModel>> availableJobsByPriority)
    {
        return convertJobAssignmentsIntoJobAssignmentOperations(JobAssignmentAction.GIVE, jobsAssignmentsToGive,
                availableJobsByPriority);
    }

    private List<JobAssignmentOperation> convertJobAssignmentsIntoJobAssignmentOperations(JobAssignmentAction action,
            JobAssignments jobsAssignments, Map<Integer, List<JobModel>> jobsByPriority)
    {
        List<JobAssignmentOperation> operations = new LinkedList<>();
        Set<WorkerModel> workers = jobsAssignments.getWorkers();
        for (WorkerModel worker : workers)
        {
            Map<Integer, Integer> priorityCounts = jobsAssignments.getJobPriorityCounts(worker);
            for (int priority : priorityCounts.keySet())
            {
                while (priorityCounts.get(priority) > 0)
                {
                    JobModel job = jobsByPriority.get(priority).remove(0);

                    JobAssignmentOperation jobOperation = new JobAssignmentOperation(action, worker.getWorkerID(),
                            job.getJobID());
                    operations.add(jobOperation);

                    priorityCounts.put(priority, priorityCounts.get(priority) - 1);
                }
            }
        }
        return operations;
    }

}
