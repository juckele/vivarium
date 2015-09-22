package com.johnuckele.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.World;

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
        LinkedList<Option> options = new LinkedList<Option>();
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
        Blueprint blueprint;
        if (commandLine.hasOption(BLUEPRINT_INPUT_FILE))
        {
            String blueprintFile = null;
            try
            {
                blueprintFile = commandLine.getOptionValue(BLUEPRINT_INPUT_FILE);
                blueprint = (Blueprint) ScriptIO.loadObject(blueprintFile, Format.JSON);
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "blueprint file " + blueprintFile
                        + " does not contain a blueprint as a top level object";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        else
        {
            blueprint = Blueprint.makeDefault();
        }
        if (commandLine.hasOption(SIZE_OPTION))
        {
            int size = Integer.parseInt(commandLine.getOptionValue(SIZE_OPTION));
            blueprint.setSize(size);
        }

        // Build the world
        World world = new World(blueprint);

        // Save the world
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        ScriptIO.saveSerializer(world, outputFile, Format.JSON);
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
