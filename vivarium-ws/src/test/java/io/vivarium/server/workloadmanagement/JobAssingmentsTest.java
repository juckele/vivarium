/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server.workloadmanagement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.johnuckele.vtest.Tester;

import io.vivarium.persistence.WorkerModel;

public class JobAssingmentsTest
{
    @Test
    public void testScoreCalculationsForSingleWorker()
    {
        // Mock a worker
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });

        // Build the job assignments object
        JobAssignments jobAssignments = new JobAssignments(Lists.newArrayList(worker1));

        long score;
        long scoreImprovement;

        // Test scores & add jobs

        // Should start with a score of zero
        score = jobAssignments.getScore();
        Tester.equal("Initial score should be zero", score, 0);

        // Adding a priority 3 job should increase the score 45 (with a new value of 45).
        // 45 = 15 (the first slot) * 3 (the priority)
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker1, 3);
        Tester.equal("Adding a priority 3 job should improve the score by 45", scoreImprovement, 45);
        jobAssignments.addWorkerJob(worker1, 3);
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 45", score, 45);

        // Explore adding a 1 priority job. This would give a total score of around 50, but due to rounding will
        // actually be 48, (25/2 = 12, 12 * 3 + 12 * 1 = 48).
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker1, 1);
        Tester.equal("Adding a priority 1 job should improve the score by 3", scoreImprovement, 3);

        // Seems uncompelling to add a priority one job, but maybe a priort 3 job just came in...
        // 25 / 2 * 2 * 3 = 72, so we would count a priority 3 job as a +27 improvement to score
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker1, 3);
        Tester.equal("Adding a priority 3 job should improve the score by 27", scoreImprovement, 27);
        jobAssignments.addWorkerJob(worker1, 3);
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 72", score, 72);

        // What if we thought about adding that priority one job now?
        // 30 / 3 = 10, 10 * 2 * 3 = 60, 10 * 1 * 1 = 10. 60 + 10 = 70. Actually decreases score!
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker1, 1);
        Tester.equal("Adding a priority 1 job should decrease the score by 2", scoreImprovement, -2);
        jobAssignments.addWorkerJob(worker1, 1);
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 70", score, 70);
    }

    @Test
    public void testScoreCalculationsForMultiWorker()
    {
        // Mock a few workers
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 50, 60, 70 });
        WorkerModel worker3 = mock(WorkerModel.class);
        when(worker3.getThroughputs()).thenReturn(new long[] { 20, 22, 23, 24, 25 });

        // Build the job assignments object
        JobAssignments jobAssignments = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));

        long score;
        long scoreImprovement;

        // Test scores & add jobs

        // Should start with a score of zero
        score = jobAssignments.getScore();
        Tester.equal("Initial score should be zero", score, 0);

        // Add priority 1 jobs to each worker
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker1, 1);
        Tester.equal("Adding a priority 1 job  to worker 1 should improve the score by 15", scoreImprovement, 15);
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker2, 1);
        Tester.equal("Adding a priority 1 job  to worker 2 should improve the score by 15", scoreImprovement, 50);
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker3, 1);
        Tester.equal("Adding a priority 1 job  to worker 3 should improve the score by 15", scoreImprovement, 20);

        // Adding a job to a worker doesn't matter which order they occur in, so we can test every worker and then add
        // and see the same improvements.
        jobAssignments.addWorkerJob(worker1, 1);
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 15", score, 15);
        jobAssignments.addWorkerJob(worker2, 1);
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 15", score, 65);
        jobAssignments.addWorkerJob(worker3, 1);
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 15", score, 85);

        // Show that a late high priority addition can significantly improve score
        jobAssignments.addWorkerJob(worker3, 1);
        jobAssignments.addWorkerJob(worker3, 1);
        jobAssignments.addWorkerJob(worker3, 1);
        // Total score for worker 3 should now be 24, so total for the group should be 89
        score = jobAssignments.getScore();
        Tester.equal("Total score should now be 89", score, 89);
        // Adding one more priority 1 job to worker 3 would be a score improvement of 1
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker3, 1);
        Tester.equal("Adding a final priority 1 job to worker 3 should improve the score by 1", scoreImprovement, 1);
        // However, adding a priority 10 job (25 / 5 = 5, 5 * 4 * 1 = 20, 5 * 1 * 10 = 10, 20 + 10 = 30), should improve
        // the score by 6.
        scoreImprovement = jobAssignments.getScoreChangeForJob(worker3, 2);
        Tester.equal("Adding a priority 10 job to worker 3 should improve the score by 6", scoreImprovement, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingJobsToNonExistantWorker()
    {
        // Mock a couple of workers
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 50, 60, 70 });

        // Build the job assignments object
        JobAssignments jobAssignments = new JobAssignments(Lists.newArrayList(worker1));

        // Add a Worker job with a worker we did not give to the JobAssignments object
        jobAssignments.addWorkerJob(worker2, 1);
        Tester.fail("The above code should have failed.");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testOverloadingWorkerWithJobs()
    {
        // Mock a worker
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });

        // Build the job assignments object
        JobAssignments jobAssignments = new JobAssignments(Lists.newArrayList(worker1));

        // Add a too many jobs
        for (int i = 0; i < 100; i++)
        {
            jobAssignments.addWorkerJob(worker1, 3);
        }
        Tester.fail("The above code should have failed.");
    }
}
