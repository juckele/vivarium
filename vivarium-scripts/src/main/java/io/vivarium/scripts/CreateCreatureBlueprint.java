package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.SerializationEngine;

public class CreateCreatureBlueprint extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";

    public CreateCreatureBlueprint(String[] args)
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
                .desc("file to save to creature blueprint to")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        Map<String, Object> extraOptions = this.extraArgsAsMap(commandLine);

        // Build the creature blueprint
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        new SerializationEngine().deserialize(creatureBlueprint, extraOptions);

        // Save the blueprint
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(creatureBlueprint, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating creature blueprints.";
    }

    @Override
    protected String getExtraArgString()
    {
        return " [key value [key value ...]]";
    }

    public static void main(String[] args)
    {
        new CreateCreatureBlueprint(args);
    }
}
