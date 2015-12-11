/*
 * a * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.experiment;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

import io.vivarium.client.TaskClient;
import io.vivarium.client.task.CreateJobTask;
import io.vivarium.client.task.UploadResourceTask;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.net.jobs.CreateWorldJob;
import io.vivarium.net.jobs.Job;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.util.UUID;

public class NormalizationConvergence
{
    private static int worldSize = 50;
    private static int worldCountPerGroup = 1; // 24
    private static long totalTicksPerWorld = 100; // 20_000_000
    private static long snapshotsPerWorld = 5; // 100

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
        TaskClient defaultUploadClient = new TaskClient(new UploadResourceTask(defaultBlueprint));
        defaultUploadClient.connect();
        FileIO.saveSerializer(defaultBlueprint, "defaultBlueprint.viv", Format.JSON);

        // Make the blueprint with the normalizing behavior
        Blueprint normalizingBlueprint = Blueprint.makeDefault();
        normalizingBlueprint.setSize(50);
        ArrayList<Species> normalizingSpeciesList = new ArrayList<Species>();
        Species normalizingSpecies = Species.makeDefault();
        normalizingSpecies.setNormalizeAfterMutation(true);
        normalizingSpeciesList.add(normalizingSpecies);
        normalizingBlueprint.setSpecies(normalizingSpeciesList);

        // Save the normalizing blueprint
        TaskClient normalizingUploadClient = new TaskClient(new UploadResourceTask(normalizingBlueprint));
        normalizingUploadClient.connect();
        FileIO.saveSerializer(defaultBlueprint, "defaultBlueprint.viv", Format.JSON);

        // Start 20 simulations!
        for (int i = 1; i <= worldCountPerGroup; i++)
        {
            // Create world creation jobs
            Job createDefaultWorldJob = new CreateWorldJob(new LinkedList<>(), defaultBlueprint.getUUID(),
                    UUID.randomUUID());
            TaskClient createDefaultWorlClient = new TaskClient(new CreateJobTask(createDefaultWorldJob));
            createDefaultWorlClient.connect();

            // Create world creation jobs
            Job createNormalizingWorldJob = new CreateWorldJob(new LinkedList<>(), normalizingBlueprint.getUUID(),
                    UUID.randomUUID());
            TaskClient createNormalizingWorlClient = new TaskClient(new CreateJobTask(createNormalizingWorldJob));
            createNormalizingWorlClient.connect();

            // Thread t1 = new WorldRunnerThread("default", i, 20, 1000000);
            // t1.start();
            // Thread t2 = new WorldRunnerThread("normalizing", i, 20, 1000000);
            // t2.start();
        }
    }
}
