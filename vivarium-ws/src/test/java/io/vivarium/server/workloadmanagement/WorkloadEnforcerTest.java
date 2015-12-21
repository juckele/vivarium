/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server.workloadmanagement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.johnuckele.vtest.Tester;

import io.vivarium.persistence.JobModel;
import io.vivarium.persistence.PersistenceModule;
import io.vivarium.persistence.WorkerModel;
import io.vivarium.server.ClientConnectionManager;
import io.vivarium.util.UUID;

public class WorkloadEnforcerTest
{
    @Test
    public void testBuildDesiredJobAssingmentsWithInfiniteJobs() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // Build enforcer and get an accessible handle to the buildDesiredJobAssingments method.
        WorkloadEnforcer enforcer = new WorkloadEnforcer(mock(PersistenceModule.class),
                mock(ClientConnectionManager.class));
        Method buildDesiredJobAssingments = WorkloadEnforcer.class.getDeclaredMethod("buildDesiredJobAssingments",
                Collection.class, List.class);
        buildDesiredJobAssingments.setAccessible(true);

        // Mock a few workers and put them in a list
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 150, 250, 300, 340, 375, 405, 420 });
        when(worker1.getWorkerID()).thenReturn(UUID.randomUUID());
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 500, 600, 750 });
        when(worker2.getWorkerID()).thenReturn(UUID.randomUUID());
        WorkerModel worker3 = mock(WorkerModel.class);
        when(worker3.getThroughputs()).thenReturn(new long[] { 200, 220, 230, 240, 300 });
        when(worker3.getWorkerID()).thenReturn(UUID.randomUUID());
        Collection<WorkerModel> workers = Lists.newArrayList(worker1, worker2, worker3);

        // Build a list of jobs
        List<JobModel> jobs = new LinkedList<>();
        for (int i = 0; i < 100; i++)
        {
            JobModel job = mock(JobModel.class);
            when(job.getPriority()).thenReturn(1);
            jobs.add(job);
        }

        // Build a job assignments model
        JobAssignments jobAssignments = (JobAssignments) buildDesiredJobAssingments.invoke(enforcer, workers, jobs);

        // The model should have a score that is the sum of the maximum throughputs of all workers (420 + 750 + 300 =
        // 1470)
        long score = jobAssignments.getScore();
        Tester.equal("Total score should be the sum of the max values for each worker: ", score, 1470);
    }

    @Test
    public void testBuildDesiredJobAssingmentsWithLimitedHighPriorityJobs() throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // Build enforcer and get an accessible handle to the buildDesiredJobAssingments method.
        WorkloadEnforcer enforcer = new WorkloadEnforcer(mock(PersistenceModule.class),
                mock(ClientConnectionManager.class));
        Method buildDesiredJobAssingments = WorkloadEnforcer.class.getDeclaredMethod("buildDesiredJobAssingments",
                Collection.class, List.class);
        buildDesiredJobAssingments.setAccessible(true);

        // Mock a few workers and put them in a list
        WorkerModel worker1 = mock(WorkerModel.class);
        when(worker1.getThroughputs()).thenReturn(new long[] { 150, 250, 300, 340, 375, 405, 420 });
        when(worker1.getWorkerID()).thenReturn(UUID.randomUUID());
        WorkerModel worker2 = mock(WorkerModel.class);
        when(worker2.getThroughputs()).thenReturn(new long[] { 500, 600, 750 });
        when(worker2.getWorkerID()).thenReturn(UUID.randomUUID());
        WorkerModel worker3 = mock(WorkerModel.class);
        when(worker3.getThroughputs()).thenReturn(new long[] { 200, 220, 230, 240, 300 });
        when(worker3.getWorkerID()).thenReturn(UUID.randomUUID());
        Collection<WorkerModel> workers = Lists.newArrayList(worker1, worker2, worker3);

        // Build a list of jobs, 3 priority 10 jobs and 3 priority 1 jobs
        List<JobModel> jobs = new LinkedList<>();
        for (int i = 0; i < 3; i++)
        {
            JobModel job = mock(JobModel.class);
            when(job.getPriority()).thenReturn(10);
            jobs.add(job);
        }
        for (int i = 0; i < 3; i++)
        {
            JobModel job = mock(JobModel.class);
            when(job.getPriority()).thenReturn(1);
            jobs.add(job);
        }

        // Build a job assignments model
        JobAssignments jobAssignments = (JobAssignments) buildDesiredJobAssingments.invoke(enforcer, workers, jobs);

        // The model should assign the priority 10 jobs in this order: worker2 (with a score of 5000), worker3 (with a
        // score of 2000), and finally worker1 (with a score of 1500). There are still 3 unassigned jobs. Adding a
        // priority 1 job to any of the workers would decrease the score though, so these should not get assigned.
        long score = jobAssignments.getScore();
        Tester.equal("Total score should be the sum of the firsts slot from each worker: ", score, 8500);
    }
}
