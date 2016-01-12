package io.vivarium.net.jobs;

import java.util.LinkedList;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnuckele.vtest.Tester;

import io.vivarium.util.UUID;

public class SimulationJobTest
{
    @Test
    public void testSerializeDeserialize() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimulationJob createJob = new SimulationJob(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 100L);
        String jsonEncoding = mapper.writeValueAsString(createJob);
        Job decodedJob = mapper.readValue(jsonEncoding, Job.class);
        Tester.equal("Decoded object should be the same as the original object", createJob, decodedJob);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        SimulationJob job1;
        Job job2;

        UUID jobID = UUID.randomUUID();
        job1 = new SimulationJob(JobType.RUN_SIMULATION, jobID, new LinkedList<>(), new LinkedList<>(),
                new LinkedList<>(), 100L);
        job2 = new SimulationJob(JobType.RUN_SIMULATION, jobID, new LinkedList<>(), new LinkedList<>(),
                new LinkedList<>(), 100L);
        Tester.equal("Two jobs that are the same are equal", job1, job2);

        job1 = new SimulationJob(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 100L);
        job2 = new SimulationJob(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 100L);
        Tester.notEqual("Two jobs with different workerIDs should not be equal", job1, job2);

        job1 = new SimulationJob(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 100L);
        job2 = null;
        Tester.equal("Jobs are equal to themselves", job1, job1);
        Tester.notEqual("Jobs are not equal to null", job1, job2);
        Tester.notEqual("Jobs are not equal to null", job2, job1);
    }
}
