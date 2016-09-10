package io.vivarium.experiment;

import java.util.ArrayList;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.scripts.CreateWorld;
import io.vivarium.scripts.RunSimulation;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.util.Rand;
import io.vivarium.util.concurrency.ThreadRandAllocator;

public class NormalizationConvergenceLocal
{
    private static final int THREADS_PER_BATCH = 4;
    private static final int BATCH_COUNT = 24;
    private static Thread[] defaultThreads = new Thread[THREADS_PER_BATCH];
    private static Thread[] normalizingThreads = new Thread[THREADS_PER_BATCH];
    private static Thread[] longNormalizingThreads = new Thread[THREADS_PER_BATCH];

    /**
     * Runs the NormalizationConvergence experiment.
     *
     * Hypothesis: Normalizing creature genomes will cause speedier convergence and produce creatures with higher
     * average fitness by reducing genetic drift in 'inactive' genes. Genetic drift in inactive genes is hypothesized to
     * be dangerous because offspring have the risk of inheriting an active version when recombined from parents with
     * sufficiently distant inactive genes (this is a product of the Gaussian recombination of scalar genes used in
     * offspring creation)
     *
     * @param args
     * @throws InterruptedException
     *             Not really...
     */
    public static void main(String[] args) throws InterruptedException
    {
        // If we're running multi-threaded code, we need to use a multi-threaded random allocator
        Rand.setAllocator(new ThreadRandAllocator());

        // Make the blueprints with the default behavior
        WorldBlueprint defaultWorldBlueprint = WorldBlueprint.makeDefault();
        defaultWorldBlueprint.setSize(50);
        ArrayList<CreatureBlueprint> defaultCreatureBlueprints = new ArrayList<>();
        CreatureBlueprint defaultCreatureBlueprint = CreatureBlueprint.makeDefault();
        defaultCreatureBlueprints.add(defaultCreatureBlueprint);
        defaultWorldBlueprint.setCreatureBlueprints(defaultCreatureBlueprints);

        // Save the default blueprints
        FileIO.saveSerializer(defaultWorldBlueprint, "defaultBlueprint.viv", Format.JSON);

        // Make the blueprints with the normalizing behavior
        WorldBlueprint normalizingWorldBlueprint = WorldBlueprint.makeDefault();
        normalizingWorldBlueprint.setSize(50);
        ArrayList<CreatureBlueprint> normalizingCreatureBlueprints = new ArrayList<>();
        CreatureBlueprint normalizingCreatureBlueprint = CreatureBlueprint.makeDefault();
        ((NeuralNetworkBlueprint) normalizingCreatureBlueprint.getProcessorBlueprints()[0])
                .setNormalizeAfterMutation(1);
        normalizingCreatureBlueprints.add(normalizingCreatureBlueprint);
        normalizingWorldBlueprint.setCreatureBlueprints(normalizingCreatureBlueprints);

        // Save the normalizing blueprints
        FileIO.saveSerializer(normalizingWorldBlueprint, "normalizingBlueprint.viv", Format.JSON);

        // Make the blueprints with the normalizing behavior and a longer length
        WorldBlueprint longNormalizingWorldBlueprint = WorldBlueprint.makeDefault();
        longNormalizingWorldBlueprint.setSize(50);
        ArrayList<CreatureBlueprint> longNormalizingCreatureBlueprints = new ArrayList<>();
        CreatureBlueprint longNormalizingCreatureBlueprint = CreatureBlueprint.makeDefault();
        ((NeuralNetworkBlueprint) longNormalizingCreatureBlueprint.getProcessorBlueprints()[0])
                .setNormalizeAfterMutation(Math.sqrt(42));
        longNormalizingCreatureBlueprints.add(longNormalizingCreatureBlueprint);
        longNormalizingWorldBlueprint.setCreatureBlueprints(longNormalizingCreatureBlueprints);

        // Save the normalizing blueprints
        FileIO.saveSerializer(longNormalizingWorldBlueprint, "longBlueprint.viv", Format.JSON);

        // Run all the simulation batches
        System.out.println("Starting Simulation");
        for (int batchIndex = 0; batchIndex < BATCH_COUNT; batchIndex++)
        {
            System.out.println("Starting Batch: " + batchIndex);
            // Start a batch of simulations
            for (int threadIndex = 0; threadIndex < THREADS_PER_BATCH; threadIndex++)
            {
                int threadNumber = THREADS_PER_BATCH * batchIndex + threadIndex + 1;
                defaultThreads[threadIndex] = new WorldRunnerThread("default", threadNumber, 50, 2_000_000);
                normalizingThreads[threadIndex] = new WorldRunnerThread("normalizing", threadNumber, 50, 2_000_000);
                longNormalizingThreads[threadIndex] = new WorldRunnerThread("long", threadNumber, 50, 2_000_000);
                defaultThreads[threadIndex].start();
                normalizingThreads[threadIndex].start();
                longNormalizingThreads[threadIndex].start();
            }
            System.out.println("Awaiting Batch: " + batchIndex);
            // And wait for them all to finish
            for (int threadIndex = 1; threadIndex < THREADS_PER_BATCH; threadIndex++)
            {
                defaultThreads[threadIndex].join();
                normalizingThreads[threadIndex].join();
                longNormalizingThreads[threadIndex].join();
            }
            System.out.println("Completed Batch: " + batchIndex);
        }
        System.out.println("Completed Simulation.");
    }

    private static class WorldRunnerThread extends Thread
    {
        private final String _prefix;
        private final int _iteration;
        private final int _steps;
        private final int _ticksPerStep;

        WorldRunnerThread(String prefix, int iteration, int steps, int ticksPerStep)
        {
            this._prefix = prefix;
            this._iteration = iteration;
            this._steps = steps;
            this._ticksPerStep = ticksPerStep;
        }

        @Override
        public void run()
        {
            // Create a world
            {
                String[] args = { "-b", _prefix + "Blueprint.viv", "-o",
                        _prefix + "World" + _iteration + "_step" + 0 + ".viv" };
                CreateWorld.main(args);
            }

            // Run the world $steps times
            for (int i = 0; i < _steps; i++)
            {
                // for $ticksPerStep each time
                String[] args = { "-i", _prefix + "World" + _iteration + "_step" + i + ".viv", "-o",
                        _prefix + "World" + _iteration + "_step" + (i + 1) + ".viv", "-t", "" + _ticksPerStep };
                RunSimulation.main(args);
            }
        }
    }
}
