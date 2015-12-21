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

        // Build the job assingments object
        JobAssingments jobAssingments = new JobAssingments(Lists.newArrayList(worker1));

        long score;
        long scoreImprovement;
        // Test scores & add jobs

        // Should start with a score of zero
        score = jobAssingments.getScore();
        Tester.equal("Initial score should be zero", score, 0);

        // Adding a priority 3 job should increase the score 45 (with a new value of 45).
        // 45 = 15 (the first slot) * 3 (the priority)
        scoreImprovement = jobAssingments.getScoreChangeForJob(worker1, 3);
        Tester.equal("Adding a priority 3 job should improve the score by 45", scoreImprovement, 45);
        jobAssingments.addWorkerJob(worker1, 3);
        score = jobAssingments.getScore();
        Tester.equal("Total score should now be 45", score, 45);

        // Explore adding a 1 priority job. This would give a total score of around 50, but due to rounding will
        // actually be 48, (25/2 = 12, 12 * 3 + 12 * 1 = 48).
        scoreImprovement = jobAssingments.getScoreChangeForJob(worker1, 1);
        Tester.equal("Adding a priority 1 job should improve the score by 3", scoreImprovement, 3);
    }
}
