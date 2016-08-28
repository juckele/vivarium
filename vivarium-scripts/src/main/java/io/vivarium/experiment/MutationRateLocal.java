package io.vivarium.experiment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.vivarium.audit.ActionFrequencyBlueprint;
import io.vivarium.audit.AuditBlueprint;
import io.vivarium.audit.CensusBlueprint;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.scripts.CreateWorld;
import io.vivarium.scripts.RunSimulation;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.util.Functions;
import io.vivarium.util.Rand;
import io.vivarium.util.concurrency.ThreadRandAllocator;

public class MutationRateLocal
{
    private static final int WORLD_SIZE = 100;
    private static final int LIFE_TIMES_PER_SIMULATION = 1000;
    private static final int TICKS_PER_SIMULATION = LIFE_TIMES_PER_SIMULATION * 20_000;
    private static final int MAX_SIMULATIONS = 101;
    private static final int PEAK_THREAD_THROUGHPUT = 4;
    private static final int MIN_MUTATION_EXPONENT = -15;
    private static final int MAX_MUTATION_EXPONENT = -5;

    /**
     * Runs the MutationRate experiment.
     *
     * Hypothesis: There is an optimal mutation rate for population health.
     *
     * @param args
     * @throws InterruptedException
     *             Not really...
     */
    public static void main(String[] args) throws InterruptedException
    {
        // If we're running multi-threaded code, we need to use a multi-threaded random allocator
        Rand.setAllocator(new ThreadRandAllocator());

        // Set up thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(PEAK_THREAD_THROUGHPUT);
        Collection<WorldRunner> tasks = new LinkedList<>();

        double[] mutationRates = Functions.generateDitherArray(MIN_MUTATION_EXPONENT, MAX_MUTATION_EXPONENT,
                MAX_SIMULATIONS);
        for (int i = 0; i < MAX_SIMULATIONS; i++)
        {
            double mutationRateExponent = Math.round(mutationRates[i] * 100) / 100.0;

            // Record the thread name
            String name = "mutation=2^" + mutationRateExponent;

            // Make a world blueprint
            WorldBlueprint worldBlueprint = WorldBlueprint.makeDefault();
            worldBlueprint.setSize(WORLD_SIZE);
            // Set creature blueprint
            ArrayList<CreatureBlueprint> creatureBlueprints = new ArrayList<>();
            CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
            NeuralNetworkBlueprint processorBlueprint = (NeuralNetworkBlueprint) creatureBlueprint
                    .getProcessorBlueprint();
            processorBlueprint.setMutationRateExponent(mutationRateExponent);
            creatureBlueprint.getProcessorBlueprint().setNormalizeAfterMutation(Math.sqrt(42));
            creatureBlueprints.add(creatureBlueprint);
            worldBlueprint.setCreatureBlueprints(creatureBlueprints);
            // Set audit functions
            ArrayList<AuditBlueprint> auditBlueprints = new ArrayList<>();
            auditBlueprints.add(new ActionFrequencyBlueprint());
            auditBlueprints.add(new CensusBlueprint());
            worldBlueprint.setAuditBlueprints(auditBlueprints);

            // Save the world blueprint
            FileIO.saveSerializer(worldBlueprint, name + "_blueprint.viv", Format.JSON);

            // Create callable
            log("Generating world blueprint " + name);
            tasks.add(new WorldRunner(name));
        }

        // Do the work!
        executorService.invokeAll(tasks);
        log("Awating");
        executorService.shutdown();
        executorService.awaitTermination(10_000, TimeUnit.DAYS);
        log("Completed");
    }

    private static class WorldRunner implements Callable<Object>
    {

        private final String _name;

        WorldRunner(String name)
        {
            this._name = name;
        }

        @Override
        public Object call()
        {
            try
            {
                // Create a world
                {
                    log("Generating world " + _name);
                    String[] args = { "-b", _name + "_blueprint.viv", "-o", _name + "_initial.viv" };
                    CreateWorld.main(args);
                }

                // Run the world
                {
                    log("Starting simulation of world " + _name);
                    String[] args = { "-i", _name + "_initial.viv", "-o", _name + "_complete.viv", "-t",
                            "" + TICKS_PER_SIMULATION };
                    RunSimulation.main(args);
                    log("Completeing simulation of world " + _name);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    private synchronized static void log(String event)
    {
        System.out.println(event);
    }
}
