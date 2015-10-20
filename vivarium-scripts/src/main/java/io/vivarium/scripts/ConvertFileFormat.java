/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.VivariumObjectCollection;

public class ConvertFileFormat extends CommonsScript
{
    private static final String INPUT_FILE = "input";
    private static final String OUTPUT_FILE = "output";
    private static final String INPUT_FORMAT = "inputFormat";
    private static final String OUTPUT_FORMAT = "outputFormat";

    public ConvertFileFormat(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("i").required(true).longOpt(INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load").build());
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save").build());
        options.add(Option.builder("x").required(true).longOpt(INPUT_FORMAT).hasArg(true).argName("FORMAT")
                .desc("format to load").build());
        options.add(Option.builder("y").required(true).longOpt(OUTPUT_FORMAT).hasArg(true).argName("FORMAT")
                .desc("format to save").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load the file
        Format inputFormat = Format.parseFormat(commandLine.getOptionValue(INPUT_FORMAT));
        String inputFile = commandLine.getOptionValue(INPUT_FILE);
        VivariumObjectCollection collection = FileIO.loadObjectCollection(inputFile, inputFormat);

        // Save the blueprint
        Format outputFormat = Format.parseFormat(commandLine.getOptionValue(OUTPUT_FORMAT));
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializerCollection(collection, outputFile, outputFormat);
    }

    @Override
    protected String getUsageHeader()
    {
        return "Convert a vivarium file in one format containing a single top level object to a file in another format, containing the same top level object.";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new ConvertFileFormat(args);
    }

}
