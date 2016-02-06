/*
 * Copyright © 2016 John H Uckele. All rights reserved.
 */

package io.vivarium.scripts;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.processor.Processor;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.VivariumObjectCollection;

public class MeasureProcessorGenomeLengths extends CommonsScript
{
    private static final String INPUT_FILE = "input";

    public MeasureProcessorGenomeLengths(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("i").required(true).longOpt(INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load processors from.").build());
        return options;
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    @Override
    protected String getUsageHeader()
    {
        return "Measures the length of all genomes for all processors in a file and prints them in ascending order, seperated by newlines.";
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load the file
        String inputFile = commandLine.getOptionValue(INPUT_FILE);

        VivariumObjectCollection collection = FileIO.loadObjectCollection(inputFile, Format.JSON);
        Collection<Processor> processors = collection.getAll(Processor.class);

        double[] lengths = run(processors);

        for (double length : lengths)
        {
            System.out.println(length);
        }
    }

    public double[] run(Collection<Processor> processors)
    {
        double[] lengths = new double[processors.size()];
        int lengthIndex = 0;
        for (Processor processor : processors)
        {
            lengths[lengthIndex++] = processor.getGenomeLength();
        }
        Arrays.sort(lengths);
        return lengths;
    }

    public static void main(String[] args)
    {
        new MeasureProcessorGenomeLengths(args);
    }
}
