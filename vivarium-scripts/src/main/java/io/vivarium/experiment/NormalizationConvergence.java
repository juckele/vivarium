/*
 * a * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.experiment;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.google.common.collect.Lists;

import io.vivarium.client.TaskClient;
import io.vivarium.client.task.CreateJobTask;
import io.vivarium.client.task.UploadResourceTask;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.net.jobs.CreateWorldJob;
import io.vivarium.net.jobs.Job;
import io.vivarium.net.jobs.SimulationJob;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;

public class NormalizationConvergence
{
    private static int worldSize = 50;
    private static int worldCountPerGroup = 1; // 24
    private static long totalTicksPerWorld = 100; // 20_000_000
    private static int snapshotsPerWorld = 5; // 100
    private static long ticksPerSnapshot = totalTicksPerWorld / snapshotsPerWorld;

    /**
     * Sets up the NormalizationConvergence experiment on a Vivarium research server.
     *
     * Hypothesis: Normalizing creature genomes will cause speedier convergence and produce creatures with higher
     * average fitness by reducing genetic drift in 'inactive' genes. Genetic drift in inactive genes is hypothesized to
     * be dangerous because offspring have the risk of inheriting an active version when recombined from parents with
     * sufficiently distant inactive genes (this is a product of the Gaussian recombination of scalar genes used in
     * offspring creation)
     *
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws URISyntaxException
    {
        // Make the blueprint with the default behavior
        Blueprint defaultBlueprint = Blueprint.makeDefault();
        defaultBlueprint.setSize(worldSize);
        ArrayList<Species> defaultSpeciesList = new ArrayList<Species>();
        Species defaultSpecies = Species.makeDefault();
        defaultSpeciesList.add(defaultSpecies);
        defaultBlueprint.setSpecies(defaultSpeciesList);

        // Save the default blueprint
        TaskClient defaultUploadClient = new TaskClient(
                new UploadResourceTask(defaultBlueprint.getUUID(), defaultBlueprint));
        defaultUploadClient.connect();

        // Make the blueprint with the normalizing behavior
        Blueprint normalizingBlueprint = Blueprint.makeDefault();
        normalizingBlueprint.setSize(50);
        ArrayList<Species> normalizingSpeciesList = new ArrayList<Species>();
        Species normalizingSpecies = Species.makeDefault();
        normalizingSpecies.setNormalizeAfterMutation(true);
        normalizingSpeciesList.add(normalizingSpecies);
        normalizingBlueprint.setSpecies(normalizingSpeciesList);

        // Save the normalizing blueprint
        TaskClient normalizingUploadClient = new TaskClient(
                new UploadResourceTask(normalizingBlueprint.getUUID(), normalizingBlueprint));
        normalizingUploadClient.connect();

        // Pre-allocate the resource snapshots
        UUID[] defaultWorldSnapshots = new UUID[snapshotsPerWorld];
        UUID[] normalizingWorldSnapshots = new UUID[snapshotsPerWorld];
        for (int i = 0; i < snapshotsPerWorld; i++)
        {
            defaultWorldSnapshots[i] = UUID.randomUUID();
            normalizingWorldSnapshots[i] = UUID.randomUUID();
            new TaskClient(new UploadResourceTask(defaultWorldSnapshots[i], new VivariumObjectCollection())).connect();
            new TaskClient(new UploadResourceTask(normalizingWorldSnapshots[i], new VivariumObjectCollection()))
                    .connect();
            carelessSleep(100);
        }

        // Make all of the simulations simulations!
        for (int i = 1; i <= worldCountPerGroup; i++)
        {
            // Create world creation jobs
            Job createDefaultWorldJob = new CreateWorldJob(Lists.newArrayList(defaultBlueprint.getUUID()),
                    Lists.newArrayList(defaultWorldSnapshots[0]), new LinkedList<>());
            TaskClient createDefaultWorlClient = new TaskClient(new CreateJobTask(createDefaultWorldJob));
            createDefaultWorlClient.connect();

            // Create world creation jobs
            Job createNormalizingWorldJob = new CreateWorldJob(Lists.newArrayList(normalizingBlueprint.getUUID()),
                    Lists.newArrayList(normalizingWorldSnapshots[0]), new LinkedList<>());
            TaskClient createNormalizingWorlClient = new TaskClient(new CreateJobTask(createNormalizingWorldJob));
            createNormalizingWorlClient.connect();
            carelessSleep(100);

            long endTick = ticksPerSnapshot;
            UUID defaultJobDependency = createDefaultWorldJob.jobID;
            UUID normalizingJobDependency = createNormalizingWorldJob.jobID;
            UUID defaultInputResource = defaultBlueprint.getUUID();
            UUID normalizingInputResource = normalizingBlueprint.getUUID();
            // Create simulation jobs
            for (int j = 0; j < snapshotsPerWorld; j++)
            {
                // Create simulation jobs
                Job defaultSimulateJob = new SimulationJob(Lists.newArrayList(defaultInputResource),
                        Lists.newArrayList(defaultWorldSnapshots[j]), Lists.newArrayList(defaultJobDependency),
                        endTick);
                new TaskClient(new CreateJobTask(defaultSimulateJob)).connect();

                // Create simulation jobs
                Job normalizingSimulateJob = new SimulationJob(Lists.newArrayList(normalizingInputResource),
                        Lists.newArrayList(normalizingWorldSnapshots[j]), Lists.newArrayList(normalizingJobDependency),
                        endTick);
                new TaskClient(new CreateJobTask(normalizingSimulateJob)).connect();
                carelessSleep(100);

                // Update dependencies for next pass
                defaultJobDependency = defaultSimulateJob.jobID;
                normalizingJobDependency = normalizingSimulateJob.jobID;
                defaultInputResource = defaultWorldSnapshots[j];
                normalizingInputResource = normalizingWorldSnapshots[j];

                // Update end tick for next pass
                endTick += ticksPerSnapshot;
            }

            // Done, wait for connections to finish writing if they need and then exit.
            System.out.println("DONE!");
            carelessSleep(5000);
            System.exit(0);
        }
    }

    private static void carelessSleep(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
