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

    @Test
    public void testSubtractForEmptyJobAssignments()
    {
        // Mock a worker
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker3 = mock(WorkerModel.class);
        when(worker3.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });

        // Build the job assignments objects
        JobAssignments jobAssignments1 = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));
        JobAssignments jobAssignments2 = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));

        JobAssignments difference = JobAssignments.subtract(jobAssignments1, jobAssignments2);
        Tester.isNotNull("Subtracted job assignments should exist", difference);
        long score = difference.getScore();
        Tester.equal("Score should be zero", score, 0);
    }

    @Test
    public void testSubtractForJobAssignments()
    {
        // Mock a worker
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker3 = mock(WorkerModel.class);
        when(worker3.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });

        // Build the job assignments objects
        JobAssignments oldAssignments = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));
        JobAssignments newAssignments = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));

        // Assign jobs on the first assignment object
        oldAssignments.addWorkerJob(worker1, 1);
        oldAssignments.addWorkerJob(worker1, 1);
        oldAssignments.addWorkerJob(worker1, 3);
        oldAssignments.addWorkerJob(worker2, 2);
        oldAssignments.addWorkerJob(worker2, 2);

        // Assign jobs on the second assignment object
        newAssignments.addWorkerJob(worker1, 3);
        newAssignments.addWorkerJob(worker1, 3);
        newAssignments.addWorkerJob(worker3, 1);
        newAssignments.addWorkerJob(worker3, 2);
        newAssignments.addWorkerJob(worker3, 2);

        // Compute both differences
        JobAssignments removedDifference = JobAssignments.subtract(oldAssignments, newAssignments);
        JobAssignments addedDifference = JobAssignments.subtract(newAssignments, oldAssignments);

        // Validate that all the differences are correct
        int worker1Priority1JobsRemoved = removedDifference.getJobPriorityCounts(worker1).get(1);
        int worker1Priority1JobsAdded = addedDifference.getJobPriorityCounts(worker1).get(1);
        Tester.equal("Worker 1 should have 2 removed priority 1 jobs", worker1Priority1JobsRemoved, 2);
        Tester.equal("Worker 1 should have 0 added priority 1 jobs", worker1Priority1JobsAdded, 0);

        int worker1Priority3JobsRemoved = removedDifference.getJobPriorityCounts(worker1).get(3);
        int worker1Priority3JobsAdded = addedDifference.getJobPriorityCounts(worker1).get(3);
        Tester.equal("Worker 1 should have 0 removed priority 3 jobs", worker1Priority3JobsRemoved, 0);
        Tester.equal("Worker 1 should have 1 added priority 3 jobs", worker1Priority3JobsAdded, 1);

        int worker2Priority2JobsRemoved = removedDifference.getJobPriorityCounts(worker2).get(2);
        int worker2Priority2JobsAdded = addedDifference.getJobPriorityCounts(worker2).get(2);
        Tester.equal("Worker 2 should have 2 removed priority 2 jobs", worker2Priority2JobsRemoved, 2);
        Tester.equal("Worker 2 should have 0 added priority 2 jobs", worker2Priority2JobsAdded, 0);

        int worker3Priority2JobsRemoved = removedDifference.getJobPriorityCounts(worker3).get(2);
        int worker3Priority2JobsAdded = addedDifference.getJobPriorityCounts(worker3).get(2);
        Tester.equal("Worker 3 should have 0 removed priority 2 jobs", worker3Priority2JobsRemoved, 0);
        Tester.equal("Worker 3 should have 2 added priority 2 jobs", worker3Priority2JobsAdded, 2);
    }

    @Test
    public void testSubtractWorkerSetValidation()
    {
        // Mock a worker
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });
        WorkerModel worker3 = mock(WorkerModel.class);
        when(worker3.getThroughputs()).thenReturn(new long[] { 15, 25, 30, 34, 37, 39, 40 });

        try
        {
            // Build the job assignments objects
            JobAssignments jobAssignments1 = new JobAssignments(Lists.newArrayList(worker1, worker2));
            JobAssignments jobAssignments2 = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));

            // This will fail because the JobAssignments objects don't share the same workers
            JobAssignments.subtract(jobAssignments1, jobAssignments2);
            Tester.fail("The above code should have failed.");
        }
        catch (IllegalArgumentException e)
        {
            Tester.pass(
                    "If the minued has an incomplete subset of workers that the subtrahend has, subtract throws an IllegalArgumentException");
        }

        try
        {
            // Build the job assignments objects
            JobAssignments jobAssignments1 = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));
            JobAssignments jobAssignments2 = new JobAssignments(Lists.newArrayList(worker2, worker3));

            // This will fail because the JobAssignments objects don't share the same workers
            JobAssignments.subtract(jobAssignments1, jobAssignments2);
            Tester.fail("The above code should have failed.");
        }
        catch (IllegalArgumentException e)
        {
            Tester.pass(
                    "If the subtrahend has an incomplete subset of workers that the minued has, subtract throws an IllegalArgumentException");
        }

        try
        {
            // Build the job assignments objects
            JobAssignments jobAssignments1 = new JobAssignments(Lists.newArrayList(worker3, worker1, worker2));
            JobAssignments jobAssignments2 = new JobAssignments(Lists.newArrayList(worker1, worker2, worker3));

            // This will fail because the JobAssignments objects don't share the same workers
            JobAssignments.subtract(jobAssignments1, jobAssignments2);
            Tester.pass(
                    "If the subtrahend and minued have the same set, the code will not throw regardless of ordering");
        }
        catch (IllegalArgumentException e)
        {
            Tester.fail("The above code should not have failed.");
        }

        try
        {
            // Build the job assignments objects
            JobAssignments jobAssignments1 = new JobAssignments(Lists.newArrayList(worker3, worker2));
            JobAssignments jobAssignments2 = new JobAssignments(Lists.newArrayList(worker1, worker3));

            // This will fail because the JobAssignments objects don't share the same workers
            JobAssignments.subtract(jobAssignments1, jobAssignments2);
            Tester.fail("The above code should have failed.");
        }
        catch (IllegalArgumentException e)
        {
            Tester.pass(
                    "If the minued and subtrahend have worker sets of the same size but with different membership, subtract throws an IllegalArgumentException");
        }
    }

}
