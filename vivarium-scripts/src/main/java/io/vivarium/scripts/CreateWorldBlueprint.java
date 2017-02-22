package io.vivarium.scripts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.GridWorldBlueprint;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.SerializationEngine;

public class CreateWorldBlueprint extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String AUDIT_INPUT_FILE = "audit";
    private static final String CREATURE_INPUT_FILE = "creature";

    public CreateWorldBlueprint(String[] args)
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
                .desc("file to save to world blueprint to")
                .build());
        options.add(Option
                .builder("a")
                .required(false)
                .longOpt(AUDIT_INPUT_FILE)
                .hasArg(true)
                .argName("FILE")
                .desc("file to load audit blueprints from. If this option is not given, no audit blueprints will be added to the world blueprint.")
                .build());
        options.add(Option
                .builder("s")
                .required(false)
                .longOpt(CREATURE_INPUT_FILE)
                .hasArg(true)
                .argName("FILE")
                .desc("file to load creature blueprints from. If this option is not given, a single default creature blueprint will be added to the world blueprint.")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        LinkedList<AuditBlueprint> auditBlueprints = new LinkedList<>();
        LinkedList<CreatureBlueprint> creatureBlueprints = new LinkedList<>();
        if (commandLine.hasOption(AUDIT_INPUT_FILE))
        {
            String auditFile = null;
            try
            {
                auditFile = commandLine.getOptionValue(AUDIT_INPUT_FILE);
                for (AuditBlueprint auditBlueprint : FileIO
                        .loadObjectCollection(auditFile, Format.JSON)
                        .getAll(AuditBlueprint.class))
                {
                    auditBlueprints.add(auditBlueprint);
                }
                if (auditBlueprints.isEmpty())
                {
                    throw new IllegalStateException("No audit blueprints found in audit input file " + auditFile);
                }
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "audit input file " + auditFile
                        + " does not contain audit blueprints as top level objects";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        if (commandLine.hasOption(CREATURE_INPUT_FILE))
        {
            String creatureFile = null;
            try
            {
                creatureFile = commandLine.getOptionValue(CREATURE_INPUT_FILE);
                for (CreatureBlueprint specie : FileIO
                        .loadObjectCollection(creatureFile, Format.JSON)
                        .getAll(CreatureBlueprint.class))
                {
                    creatureBlueprints.add(specie);
                }
                if (creatureBlueprints.isEmpty())
                {
                    throw new IllegalStateException(
                            "No creature blueprints found in creature input file " + creatureFile);
                }
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "creature input file " + creatureFile
                        + " does not contain creture blueprints as top level objects";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        Map<String, Object> extraOptions = this.extraArgsAsMap(commandLine);

        // Build the world blueprint
        GridWorldBlueprint worldBlueprint = GridWorldBlueprint.makeDefault();
        new SerializationEngine().deserialize(worldBlueprint, extraOptions);
        if (!creatureBlueprints.isEmpty())
        {
            worldBlueprint.setCreatureBlueprints(new ArrayList<>(creatureBlueprints));
        }
        if (!auditBlueprints.isEmpty())
        {
            worldBlueprint.setAuditBlueprints(new ArrayList<>(auditBlueprints));
        }

        // Save the world blueprint
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(worldBlueprint, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating world blueprints.";
    }

    @Override
    protected String getExtraArgString()
    {
        return " [key value [key value ...]]";
    }

    public static void main(String[] args)
    {
        new CreateWorldBlueprint(args);
    }
}
