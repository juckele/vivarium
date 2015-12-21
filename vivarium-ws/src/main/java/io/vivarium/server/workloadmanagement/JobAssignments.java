/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server.workloadmanagement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import io.vivarium.persistence.WorkerModel;

public class JobAssignments
{
    private Map<WorkerModel, Integer> _workerJobCounts = new HashMap<>();
    private Map<WorkerModel, Map<Integer, Integer>> _workerJobPriorityCounts = new HashMap<>();
    private Map<WorkerModel, Long> _workerScores = new HashMap<>();

    public JobAssignments(Collection<WorkerModel> workers)
    {
        for (WorkerModel worker : workers)
        {
            _workerJobCounts.put(worker, 0);
            _workerJobPriorityCounts.put(worker, new HashMap<>());
            _workerScores.put(worker, 0L);
        }
    }

    public long getScoreChangeForJob(WorkerModel workerModel, int priority)
    {
        Preconditions.checkArgument(_workerJobCounts.containsKey(workerModel));

        // Figure out the current job count for this worker
        int workerJobCount = _workerJobCounts.get(workerModel);
        int proposedWorkerJobCount = workerJobCount + 1;

        // Figure out the new score
        long newScore = determineWorkerScore(workerModel, proposedWorkerJobCount);
        long throughput = workerModel.getThroughputs()[proposedWorkerJobCount - 1];
        long throughputPerJob = throughput / proposedWorkerJobCount;
        newScore += throughputPerJob * priority;

        // Determine how the score has changed
        long scoreChange = newScore - _workerScores.get(workerModel);
        return scoreChange;
    }

    public void addWorkerJob(WorkerModel workerModel, int priority)
    {
        Preconditions.checkArgument(_workerJobCounts.containsKey(workerModel));

        // Update the job count for this worker
        int workerJobCount = _workerJobCounts.get(workerModel);
        workerJobCount++;
        _workerJobCounts.put(workerModel, workerJobCount);

        // Update the job count for this worker and priority
        int workerPriorityJobCount = _workerJobPriorityCounts.get(workerModel).getOrDefault(priority, 0);
        workerPriorityJobCount++;
        _workerJobPriorityCounts.get(workerModel).put(priority, workerPriorityJobCount);

        // Update the score
        long newScore = determineWorkerScore(workerModel, workerJobCount);
        _workerScores.put(workerModel, newScore);
    }

    private int determineWorkerJobCount(WorkerModel workerModel)
    {
        int jobCount = 0;
        Map<Integer, Integer> jobPriorityCounts = _workerJobPriorityCounts.get(workerModel);
        for (int priority : jobPriorityCounts.keySet())
        {
            jobCount += jobPriorityCounts.get(priority);
        }
        return jobCount;
    }

    private long determineWorkerScore(WorkerModel workerModel, int workerJobCount)
    {
        if (workerJobCount == 0)
        {
            return 0;
        }
        long throughput = workerModel.getThroughputs()[workerJobCount - 1];
        long throughputPerJob = throughput / workerJobCount;
        long workerScore = 0;
        Map<Integer, Integer> jobPriorityCounts = _workerJobPriorityCounts.get(workerModel);
        for (int priorityLevel : jobPriorityCounts.keySet())
        {
            workerScore += jobPriorityCounts.get(priorityLevel) * throughputPerJob * priorityLevel;
        }

        return workerScore;
    }

    public long getScore()
    {
        long totalScore = 0;
        for (long workerScore : _workerScores.values())
        {
            totalScore += workerScore;
        }
        return totalScore;
    }

    public boolean isWorkerFull(WorkerModel workerModel)
    {
        int jobCount = _workerJobCounts.get(workerModel);
        int jobSlots = workerModel.getThroughputs().length;
        return jobCount == jobSlots;
    }

    public Set<WorkerModel> getWorkers()
    {
        return Sets.newHashSet(_workerJobCounts.keySet());
    }

    public Map<Integer, Integer> getJobPriorityCounts(WorkerModel workerModel)
    {
        Map<Integer, Integer> copyOfJobPriorityCounts = new HashMap<>(_workerJobPriorityCounts.get(workerModel));
        return copyOfJobPriorityCounts;
    }

    /**
     * Subtracts one JobAssignments object from another. Both the minuend and subtrahend should apply to the same set of
     * WorkerModels. The resulting difference is computed by comparing each workers job priority counts and subtracting
     * each value from each key. If a value would be negative, it is instead zero.
     *
     * @param minuend
     *            The JobAssignment object to subtract from.
     * @param subtrahend
     *            The JobAssignment object to subtract with.
     * @return The difference between two JobAssignment objects.
     */
    public static JobAssignments subtract(JobAssignments minuend, JobAssignments subtrahend)
    {
        // Check that these both the minuend and subtrahend have the same key set of workers.
        Preconditions.checkArgument(
                Sets.symmetricDifference(minuend._workerJobCounts.keySet(), subtrahend._workerJobCounts.keySet())
                        .size() == 0);

        // Create a new JobAssignments objects with the same worker key set.
        Set<WorkerModel> workers = minuend._workerJobCounts.keySet();
        JobAssignments difference = new JobAssignments(workers);

        // Update the difference for each worker
        for (WorkerModel worker : workers)
        {
            // Determine all present priority counts from both the minuend and subtrahend
            Map<Integer, Integer> minuendPriorityCounts = minuend.getJobPriorityCounts(worker);
            Map<Integer, Integer> subtrahendPriorityCounts = subtrahend.getJobPriorityCounts(worker);
            Set<Integer> jobPriorities = Sets.union(minuendPriorityCounts.keySet(), subtrahendPriorityCounts.keySet());

            // Create an updated workerJobPriorityCount entry
            for (int priority : jobPriorities)
            {
                int minuendPriorityCount = minuendPriorityCounts.getOrDefault(priority, 0);
                int subtrahendPriorityCount = subtrahendPriorityCounts.getOrDefault(priority, 0);
                int priorityDifference = Math.max(0, minuendPriorityCount - subtrahendPriorityCount);
                difference._workerJobPriorityCounts.get(worker).put(priority, priorityDifference);
            }

            // Update the total job counts and scores
            int workerJobCount = difference.determineWorkerJobCount(worker);
            difference._workerJobCounts.put(worker, workerJobCount);
            long score = difference.determineWorkerScore(worker, workerJobCount);
            difference._workerScores.put(worker, score);
        }

        return difference;
    }
}
