package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.World;

public class RunSimulation extends CommonsScript
{
    private static final String INPUT_FILE = "input";
    private static final String OUTPUT_FILE = "output";
    private static final String TICKS = "ticks";

    public RunSimulation(String[] args)
    {
        super(args);
    }

    // WebWorld worldCopy = Streamer.get().deepCopy(world);

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("i").required(true).longOpt(INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load").build());
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save").build());
        options.add(Option.builder("t").required(true).longOpt(TICKS).hasArg(true).argName("TICKS")
                .desc("number of ticks to simulate").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        // Load the file
        String inputFile = commandLine.getOptionValue(INPUT_FILE);
        World world = (World) ScriptIO.loadObject(inputFile, Format.JSON);

        int tickCount = Integer.parseInt(commandLine.getOptionValue(TICKS));
        for (int i = 0; i < tickCount; i++)
        {
            world.tick();
        }

        // Save the blueprint
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        ScriptIO.saveSerializer(world, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "Run a vivarium world for a given number of ticks and then save the output.";
    }

    @Override
    protected String getExtraArgString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        new RunSimulation(args);
    }

}
