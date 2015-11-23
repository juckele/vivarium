/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.Blueprint;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.core.simulation.Simulation;
import io.vivarium.util.Rand;
import io.vivarium.util.ThreadRandAllocator;

public class RunBenchmark extends CommonsScript
{
    public RunBenchmark(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        return options;
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    @Override
    protected String getUsageHeader()
    {
        return "Run a benchmark to determine a computers throughput for Vivarium simulations.";
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        try
        {
            // System.out.println("Running benchmarks:");
            System.out.println("threads,cts");

            // If we're running multi-threaded code, we need to use a multi-threaded random allocator
            Rand.setAllocator(new ThreadRandAllocator());

            // Do this just to give the JIT Compiler some stuff to optimize
            threadTest(4, 10, 100);
            // inlineTest(100);

            // Now run the actual benchmarks now that the the Java VM is warmed up
            int iterations = 1000;
            int size = 40;
            double result;
            for (int threadCount = 1; threadCount <= 16; threadCount++)
            {
                result = threadTest(threadCount, iterations, size);
                System.out.println(threadCount + "," + (int) result);
                /*
                 * System.out.print(String.format("%sx%s (%s threads):%n Creature Ticks / Second: %4.3e%n%n ~ %s", size,
                 * size, threadCount, result, (int) result));
                 */

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static double threadTest(int threadPoolSize, int iterationsPerThread, int size) throws InterruptedException
    {
        double total = 0;
        BenchmarkThread[] threads = new BenchmarkThread[threadPoolSize];
        for (int i = 0; i < threadPoolSize; i++)
        {
            threads[i] = new BenchmarkThread(iterationsPerThread, size);
        }
        for (int i = 0; i < threadPoolSize; i++)
        {
            threads[i].start();
        }
        for (int i = 0; i < threadPoolSize; i++)
        {
            threads[i].join();
            total += threads[i].getResult();
        }
        return total;
    }

    private static class BenchmarkThread extends Thread
    {
        private final int _iterations;
        private final int _size;
        private double result = 0;

        public BenchmarkThread(int iterations, int size)
        {
            _iterations = iterations;
            _size = size;
        }

        public double getResult()
        {
            return result;
        }

        @Override
        public void run()
        {
            result = iterateTest(_iterations, _size);
        }
    }

    private static double iterateTest(int iterations, int size)
    {
        // Tests, by size
        double min = Double.MAX_VALUE;
        double max = 0;
        double total = 0;
        for (int i = 0; i < iterations; i++)
        {
            double testRun = inlineTest(size);
            min = Math.min(min, testRun);
            max = Math.max(max, testRun);
            total += testRun;
            // System.out.print(String.format("%4.3e ", testRun));
        }
        double average = total / iterations;
        return average;
    }

    private static double inlineTest(int size)
    {
        Blueprint blueprint = Blueprint.makeDefault();
        blueprint.setSize(size);
        blueprint.getSpecies().get(0).setMaximumFood(Integer.MAX_VALUE); // Increase max food to prevent creatures from
                                                                         // starving during the benchmark
        World world = new World(blueprint);
        int tickCount = 2000;
        int worldPopulation = world.getCount(EntityType.CREATURE);
        long startTime = System.currentTimeMillis();
        Simulation.runForUpTo(world, tickCount);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double creatureTicksPerSeconds = worldPopulation * tickCount / (totalTime / 1000.0);
        return creatureTicksPerSeconds;
    }

    public static void main(String[] args)
    {
        new RunBenchmark(args);
    }

}
