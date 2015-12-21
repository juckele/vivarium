/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server.workloadmanagement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.vivarium.persistence.WorkerModel;

public class JobAssingments
{
    private Map<WorkerModel, Integer> _workerJobCounts = new HashMap<>();
    private Map<WorkerModel, Map<Integer, Integer>> _workerJobPriorityCounts = new HashMap<>();
    private Map<WorkerModel, Long> _workerScores = new HashMap<>();

    public JobAssingments(Collection<WorkerModel> workers)
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
        // Update the job count for this worker
        int workerJobCount = _workerJobCounts.get(workerModel);
        workerJobCount++;
        _workerJobCounts.put(workerModel, workerJobCount);

        // Update the job count for this worker and priority
        if (!_workerJobPriorityCounts.containsKey(workerModel))
        {
            _workerJobPriorityCounts.put(workerModel, new HashMap<>());
        }
        int workerPriorityJobCount = _workerJobPriorityCounts.get(workerModel).getOrDefault(priority, 0);
        workerPriorityJobCount++;
        _workerJobPriorityCounts.get(workerModel).put(priority, workerPriorityJobCount);

        // Update the score
        long newScore = determineWorkerScore(workerModel, workerJobCount);
        _workerScores.put(workerModel, newScore);
    }

    private long determineWorkerScore(WorkerModel workerModel, int workerJobCount)
    {
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
}
