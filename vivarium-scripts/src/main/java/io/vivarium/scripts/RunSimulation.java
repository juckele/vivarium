/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.World;
import io.vivarium.core.simulation.Simulation;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UserFacingError;

public class RunSimulation extends CommonsScript
{
    private static final String INPUT_FILE = "input";
    private static final String OUTPUT_FILE = "output";
    private static final String TICKS = "ticks";
    private static final String SECONDS = "seconds";
    private static final String MINUTES = "minutes";
    private static final String HOURS = "hours";

    public RunSimulation(String[] args)
    {
        super(args);
    }

    // WebWorld worldCopy = Streamer.get().deepCopy(world);

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("i").required(true).longOpt(INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load, can be either a world or simulation. At least one limit, either for ticks or time, must be provided if the input is a world.")
                .build());
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save, will be the same type as the provided input").build());
        options.add(Option.builder("t").required(false).longOpt(TICKS).hasArg(true).argName("TICKS")
                .desc("max number of ticks to simulate.").build());
        options.add(Option.builder("s").required(false).longOpt(SECONDS).hasArg(true).argName("SECONDS")
                .desc("max seconds to simulate for, only one time option can be provided").build());
        options.add(Option.builder("m").required(false).longOpt(MINUTES).hasArg(true).argName("MINUTES")
                .desc("max minutes to simulate for, only one time option can be provided").build());
        options.add(Option.builder("h").required(false).longOpt(HOURS).hasArg(true).argName("HOURS")
                .desc("max hours to simulate for, only one time option can be provided").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load the file
        String inputFile = commandLine.getOptionValue(INPUT_FILE);
        VivariumObjectCollection collection = FileIO.loadObjectCollection(inputFile, Format.JSON);
        World world = collection.getFirst(World.class);
        if (world == null)
        {
            String extendedMessage = "input file " + inputFile + " does not contain a world object and cannot be run";
            throw new UserFacingError(extendedMessage);
        }

        Long maxTicks = null;
        if (commandLine.hasOption(TICKS))
        {
            maxTicks = Long.parseLong(commandLine.getOptionValue(TICKS));
        }
        Long maxTime = null;
        TimeUnit timeUnit = null;
        if (commandLine.hasOption(SECONDS))
        {
            maxTime = Long.parseLong(commandLine.getOptionValue(SECONDS));
            timeUnit = TimeUnit.SECONDS;
        }
        else if (commandLine.hasOption(MINUTES))
        {
            maxTime = Long.parseLong(commandLine.getOptionValue(MINUTES));
            timeUnit = TimeUnit.MINUTES;
        }
        else if (commandLine.hasOption(HOURS))
        {
            maxTime = Long.parseLong(commandLine.getOptionValue(HOURS));
            timeUnit = TimeUnit.HOURS;
        }

        if (maxTicks != null)
        {
            if (maxTime != null)
            {
                // Both ticks and time are specified
                Simulation.runForUpTo(world, maxTicks, maxTime, timeUnit);
            }
            else
            {
                // Only ticks are specified
                Simulation.runForUpTo(world, maxTicks);
            }
        }
        else
        {
            if (maxTime != null)
            {
                // Only time is specified
                Simulation.runForUpTo(world, maxTime, timeUnit);
            }
            else
            {
                // Nothing is specified, this will hang if run
                String extendedMessage = "A time limit or tick limit must be specified or a simulation will not halt.";
                throw new UserFacingError(extendedMessage);
            }
        }

        // Save the result;
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(world, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "Run a vivarium world for a given number of ticks and then save the output.";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new RunSimulation(args);
    }

}
