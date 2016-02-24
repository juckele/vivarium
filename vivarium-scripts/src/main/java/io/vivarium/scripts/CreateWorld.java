package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.Blueprint;
import io.vivarium.core.World;
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
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save to world to").build());
        options.add(Option.builder("b").required(false).longOpt(BLUEPRINT_INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load blueprint from. If this option is not given, a default blueprint will be created")
                .build());
        options.add(Option.builder("s").required(false).longOpt(SIZE_OPTION).hasArg(true).argName("n")
                .desc("override the blueprint for world size").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        Blueprint blueprint = null;
        if (commandLine.hasOption(BLUEPRINT_INPUT_FILE))
        {
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
        }
        Integer size = null;
        if (commandLine.hasOption(SIZE_OPTION))
        {
            size = Integer.parseInt(commandLine.getOptionValue(SIZE_OPTION));
        }

        // Build the world
        World world = run(blueprint, size);

        // Save the world
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(world, outputFile, Format.JSON);
    }

    /**
     * Creates a world given an (optional) Blueprint and (optional) world size.
     *
     * @param blueprint
     *            Optional blueprint. A default blueprint is created if this value is null.
     * @param size
     *            Optional size override. If this value is passed in, the world will be created with this size.
     * @return The new world.
     */
    public World run(Blueprint blueprint, Integer size)
    {
        if (blueprint == null)
        {
            blueprint = Blueprint.makeDefault();
        }
        if (size != null)
        {
            SerializationEngine serializer = new SerializationEngine();
            blueprint = serializer.makeCopy(blueprint);
            blueprint.setSize(size);
        }
        return new World(blueprint);
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
