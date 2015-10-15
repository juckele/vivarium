package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.brain.Brain;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.serialization.MapSerializerCollection;
import io.vivarium.serialization.SerializationCategory;

public class NormalizeBrainGenomes extends CommonsScript
{
    private static final String INPUT_FILE = "input";
    private static final String OUTPUT_FILE = "output";

    public NormalizeBrainGenomes(String[] args)
    {
        super(args);
    }

    // WebWorld worldCopy = Streamer.get().deepCopy(world);

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("i").required(true).longOpt(INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load.").build());
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save.").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load the file
        String inputFile = commandLine.getOptionValue(INPUT_FILE);

        MapSerializerCollection mapSerializerCollection = FileIO.loadObjectCollection(inputFile, Format.JSON);
        List<VivariumObject> brains = mapSerializerCollection.get(SerializationCategory.BRAIN);
        for (VivariumObject untypedBrain : brains)
        {
            Brain brain = (Brain) untypedBrain;
            brain.normalizeWeights();
        }

        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializerCollection(mapSerializerCollection, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "Normalize all brains in the loaded file";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new NormalizeBrainGenomes(args);
    }

}
