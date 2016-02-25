package io.vivarium.scripts;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.audit.ActionFrequencyRecord;
import io.vivarium.graphing.BaseGraph;
import io.vivarium.graphing.GenerationalActionGraph;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;

public class GraphGenerationalActionGraph extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String INPUT_FILE = "input";

    public GraphGenerationalActionGraph(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<>();
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save graph to").build());
        options.add(Option.builder("i").required(true).longOpt(INPUT_FILE).hasArg(true).argName("FILE")
                .desc("vivarium save file to load.").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Parse & validate arguments
        ActionFrequencyRecord record = null;
        String inputFile = commandLine.getOptionValue(INPUT_FILE);
        record = FileIO.loadObjectCollection(inputFile, Format.JSON).getFirst(ActionFrequencyRecord.class);
        if (record == null)
        {
            String extendedMessage = "file " + inputFile + " does not contain an action frequency record.";
            throw new IllegalStateException(extendedMessage);
        }
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);

        // Save a graph to the output file based on the record
        run(record, outputFile);
    }

    public void run(ActionFrequencyRecord record, String outputFileName)
    {
        BaseGraph graph = new GenerationalActionGraph(record);

        try
        {
            graph.saveImage(outputFileName, "png", 800, 800);
        }
        catch (IOException e)
        {
            String extendedMessage = "file " + outputFileName + " could not be written.";
            throw new IllegalStateException(extendedMessage, e);
        }
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for selecting or sampling creatures in world files.";
    }

    public static void main(String[] args)
    {
        new GraphGenerationalActionGraph(args);
    }

}
