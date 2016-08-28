package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.processor.Processor;
import io.vivarium.core.processor.VectorizedGenomeProcessor;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.VivariumObjectCollection;

public class NormalizeProcessorGenomes extends CommonsScript
{
    private static final String INPUT_FILE = "input";
    private static final String OUTPUT_FILE = "output";

    public NormalizeProcessorGenomes(String[] args)
    {
        super(args);
    }

    // WebWorld worldCopy = Streamer.get().deepCopy(world);

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<>();
        options.add(Option
                .builder("i")
                .required(true)
                .longOpt(INPUT_FILE)
                .hasArg(true)
                .argName("FILE")
                .desc("file to load.")
                .build());
        options.add(Option
                .builder("o")
                .required(true)
                .longOpt(OUTPUT_FILE)
                .hasArg(true)
                .argName("FILE")
                .desc("file to save.")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load the file
        String inputFile = commandLine.getOptionValue(INPUT_FILE);

        VivariumObjectCollection collection = FileIO.loadObjectCollection(inputFile, Format.JSON);
        List<Processor> processors = collection.getAll(Processor.class);
        for (Processor processor : processors)
        {
            VectorizedGenomeProcessor vectorizedProcessor = (VectorizedGenomeProcessor) processor;
            vectorizedProcessor.normalizeWeights(1);
        }

        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializerCollection(collection, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "Normalize all processors in the loaded file";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new NormalizeProcessorGenomes(args);
    }

}
