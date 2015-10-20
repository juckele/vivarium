/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.Blueprint;
import io.vivarium.core.World;
import io.vivarium.core.simulation.Simulation;
import io.vivarium.core.simulation.TickLimitHook;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;

public class CreateSimulation extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String BLUEPRINT_INPUT_FILE = "blueprint";
    private static final String WORLD_INPUT_FILE = "world";
    private static final String SIZE_OPTION = "size";
    private static final String DURATION_OPTION = "duration";

    public CreateSimulation(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save to world to").build());
        options.add(Option.builder("b").required(false).longOpt(BLUEPRINT_INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load blueprint from. If this option or world is not given, a default world with a default blueprint will be created")
                .build());
        options.add(Option.builder("w").required(false).longOpt(WORLD_INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load blueprint from. If this option or blueprint is not given, a default world with a default blueprint will be created")
                .build());
        options.add(Option.builder("s").required(false).longOpt(SIZE_OPTION).hasArg(true).argName("n")
                .desc("override the blueprint for world size. Option cannot be used with the world option").build());
        options.add(Option.builder("t").required(true).longOpt(DURATION_OPTION).hasArg(true).argName("n")
                .desc("time limit in terms of ticks to run the simulation for").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        Blueprint blueprint;
        World world;
        if (commandLine.hasOption(BLUEPRINT_INPUT_FILE))
        {
            if (commandLine.hasOption(WORLD_INPUT_FILE))
            {
                String extendedMessage = "World and blueprint file cannot both be specified";
                throw new IllegalStateException(extendedMessage);
            }
            String blueprintFile = null;
            try
            {
                blueprintFile = commandLine.getOptionValue(BLUEPRINT_INPUT_FILE);
                blueprint = FileIO.loadObjectCollection(blueprintFile, Format.JSON).getFirst(Blueprint.class);
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "blueprint file " + blueprintFile
                        + " does not contain a blueprint as a top level object";
                throw new IllegalStateException(extendedMessage, e);
            }
            if (commandLine.hasOption(SIZE_OPTION))
            {
                int size = Integer.parseInt(commandLine.getOptionValue(SIZE_OPTION));
                blueprint.setSize(size);
            }
            world = new World(blueprint);
        }
        else if (commandLine.hasOption(WORLD_INPUT_FILE))
        {
            String worldFile = null;
            try
            {
                worldFile = commandLine.getOptionValue(WORLD_INPUT_FILE);
                world = FileIO.loadObjectCollection(worldFile, Format.JSON).getFirst(World.class);
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "world file " + worldFile + " does not contain a world as a top level object";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        else
        {
            blueprint = Blueprint.makeDefault();
            if (commandLine.hasOption(SIZE_OPTION))
            {
                int size = Integer.parseInt(commandLine.getOptionValue(SIZE_OPTION));
                blueprint.setSize(size);
            }
            world = new World(blueprint);
        }

        // Create the simulation
        int ticks = Integer.parseInt(commandLine.getOptionValue(DURATION_OPTION));
        Simulation simulation = new Simulation(world);
        simulation.addHook(new TickLimitHook(ticks));

        // Save the simulation
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(simulation, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating simulation files.";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new CreateSimulation(args);
    }
}
