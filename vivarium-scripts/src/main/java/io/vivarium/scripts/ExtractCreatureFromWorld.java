package io.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.core.Creature;
import io.vivarium.core.World;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.util.Rand;

public class ExtractCreatureFromWorld extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String WORLD_INPUT_FILE = "world";
    private static final String CREATURE_ID = "id";

    public ExtractCreatureFromWorld(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save to creature to").build());
        options.add(Option.builder("w").required(true).longOpt(WORLD_INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load world from.").build());
        options.add(Option.builder("i").required(false).argName("n").longOpt(CREATURE_ID).hasArg(true)
                .desc("ID of the creature to extract from the world, if this is not set, a random creature is chosen.")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        World world = null;
        String worldFile = commandLine.getOptionValue(WORLD_INPUT_FILE);
        try
        {
            world = FileIO.loadObjectCollection(worldFile, Format.JSON).getFirst(World.class);
        }
        catch (ClassCastException e)
        {
            String extendedMessage = "blueprint file " + worldFile + " does not contain a world as a top level object";
            throw new IllegalStateException(extendedMessage, e);
        }

        Creature creature = null;
        if (commandLine.hasOption(CREATURE_ID))
        {
            int creatureID = Integer.parseInt(commandLine.getOptionValue(CREATURE_ID));
            for (Creature c : world.getCreatures())
            {
                if (c.getID() == creatureID)
                {
                    creature = c;
                }
            }
            if (creature == null)
            {
                throw new IllegalStateException("No creature with ID " + creatureID + " present in world file.");
            }
        }
        else
        {
            LinkedList<Creature> list = world.getCreatures();
            int creatureIndex = Rand.getRandomInt(list.size());
            creature = list.get(creatureIndex);
        }

        // Save the creature
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(creature, outputFile, Format.JSON);

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
        new ExtractCreatureFromWorld(args);
    }

}
