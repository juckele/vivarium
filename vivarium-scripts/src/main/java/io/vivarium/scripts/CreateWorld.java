package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.GridWorld;
import io.vivarium.core.GridWorldBlueprint;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.SerializationEngine;

public class CreateWorld extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String BLUEPRINT_INPUT_FILE = "blueprint";
    private static final String SIZE_OPTION = "size";

    public CreateWorld(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<>();
        options.add(Option
                .builder("o")
                .required(true)
                .longOpt(OUTPUT_FILE)
                .hasArg(true)
                .argName("FILE")
                .desc("file to save to world to")
                .build());
        options.add(Option
                .builder("b")
                .required(false)
                .longOpt(BLUEPRINT_INPUT_FILE)
                .hasArg(true)
                .argName("FILE")
                .desc("file to load world blueprint from. If this option is not given, a default world blueprint will be created")
                .build());
        options.add(Option
                .builder("s")
                .required(false)
                .longOpt(SIZE_OPTION)
                .hasArg(true)
                .argName("n")
                .desc("override the world blueprint for world size")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        GridWorldBlueprint worldBlueprint = null;
        if (commandLine.hasOption(BLUEPRINT_INPUT_FILE))
        {
            String blueprintFile = null;
            try
            {
                blueprintFile = commandLine.getOptionValue(BLUEPRINT_INPUT_FILE);
                worldBlueprint = FileIO.loadObjectCollection(blueprintFile, Format.JSON).getFirst(GridWorldBlueprint.class);
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "world blueprint file " + blueprintFile
                        + " does not contain a world blueprint as a top level object";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        Integer size = null;
        if (commandLine.hasOption(SIZE_OPTION))
        {
            size = Integer.parseInt(commandLine.getOptionValue(SIZE_OPTION));
        }

        // Build the world
        GridWorld world = run(worldBlueprint, size);

        // Save the world
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(world, outputFile, Format.JSON);
    }

    /**
     * Creates a world given an (optional) Blueprint and (optional) world size.
     *
     * @param worldBlueprint
     *            Optional world blueprint. A default world blueprint is created if this value is null.
     * @param size
     *            Optional size override. If this value is passed in, the world will be created with this size.
     * @return The new world.
     */
    public GridWorld run(GridWorldBlueprint worldBlueprint, Integer size)
    {
        if (worldBlueprint == null)
        {
            worldBlueprint = GridWorldBlueprint.makeDefault();
        }
        if (size != null)
        {
            SerializationEngine serializer = new SerializationEngine();
            worldBlueprint = serializer.makeCopy(worldBlueprint);
            worldBlueprint.setSize(size);
        }
        return new GridWorld(worldBlueprint);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating world files.";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new CreateWorld(args);
    }
}
