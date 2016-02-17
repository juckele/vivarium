package io.vivarium.experiment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.vivarium.audit.ActionFrequencyFunction;
import io.vivarium.audit.AuditFunction;
import io.vivarium.audit.CensusFunction;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
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
    private static final int LIFE_TIMES_PER_SIMULATION = 100;
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

            // Make a blueprint
            Blueprint blueprint = Blueprint.makeDefault();
            blueprint.setSize(WORLD_SIZE);
            // Set species
            ArrayList<Species> speciesList = new ArrayList<>();
            Species species = Species.makeDefault();
            species.setMutationRateExponent(mutationRateExponent);
            species.setNormalizeAfterMutation(Math.sqrt(42));
            speciesList.add(species);
            blueprint.setSpecies(speciesList);
            // Set audit functions
            ArrayList<AuditFunction> auditFunctions = new ArrayList<>();
            auditFunctions.add(new ActionFrequencyFunction());
            auditFunctions.add(new CensusFunction());
            blueprint.setAuditFunctions(auditFunctions);

            // Save the blueprint
            FileIO.saveSerializer(blueprint, name + "_blueprint.viv", Format.JSON);

            // Create callable
            log("Generating blueprint " + name);
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
        public Object call() throws Exception
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

            return null;
        }
    }

    private synchronized static void log(String event)
    {
        System.out.println(event);
    }
}
