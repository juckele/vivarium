package io.vivarium.scripts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.audit.AuditFunction;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.SerializationEngine;

public class CreateBlueprint extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String AUDIT_INPUT_FILE = "audit";
    private static final String SPECIES_INPUT_FILE = "species";

    public CreateBlueprint(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<>();
        options.add(Option.builder("o").required(true).longOpt(OUTPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to save to blueprint to").build());
        options.add(Option.builder("a").required(false).longOpt(AUDIT_INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load audit functions from. If this option is not given, no audit functions will be added to the blueprint.")
                .build());
        options.add(Option.builder("s").required(false).longOpt(SPECIES_INPUT_FILE).hasArg(true).argName("FILE")
                .desc("file to load species from. If this option is not given, a single default species will be added to the blueprint.")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        LinkedList<AuditFunction> auditFunctions = new LinkedList<>();
        LinkedList<Species> species = new LinkedList<>();
        if (commandLine.hasOption(AUDIT_INPUT_FILE))
        {
            String auditFile = null;
            try
            {
                auditFile = commandLine.getOptionValue(AUDIT_INPUT_FILE);
                for (AuditFunction auditFunction : FileIO.loadObjectCollection(auditFile, Format.JSON)
                        .getAll(AuditFunction.class))
                {
                    auditFunctions.add(auditFunction);
                }
                if (auditFunctions.isEmpty())
                {
                    throw new IllegalStateException("No audit functions found in audit input file " + auditFile);
                }
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "audit input file " + auditFile
                        + " does not contain audit functions as top level objects";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        if (commandLine.hasOption(SPECIES_INPUT_FILE))
        {
            String speciesFile = null;
            try
            {
                speciesFile = commandLine.getOptionValue(SPECIES_INPUT_FILE);
                for (Species specie : FileIO.loadObjectCollection(speciesFile, Format.JSON).getAll(Species.class))
                {
                    species.add(specie);
                }
                if (species.isEmpty())
                {
                    throw new IllegalStateException("No species found in species input file " + speciesFile);
                }
            }
            catch (ClassCastException e)
            {
                String extendedMessage = "species input file " + speciesFile
                        + " does not contain species as top level objects";
                throw new IllegalStateException(extendedMessage, e);
            }
        }
        Map<String, Object> extraOptions = this.extraArgsAsMap(commandLine);

        // Build the blueprint
        Blueprint blueprint = Blueprint.makeDefault();
        new SerializationEngine().deserialize(blueprint, extraOptions);
        if (!species.isEmpty())
        {
            blueprint.setSpecies(new ArrayList<>(species));
        }
        if (!auditFunctions.isEmpty())
        {
            blueprint.setAuditFunctions(new ArrayList<>(auditFunctions));
        }

        // Save the blueprint
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(blueprint, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating blueprints.";
    }

    @Override
    protected String getExtraArgString()
    {
        return " [key value [key value ...]]";
    }

    public static void main(String[] args)
    {
        new CreateBlueprint(args);
    }
}
