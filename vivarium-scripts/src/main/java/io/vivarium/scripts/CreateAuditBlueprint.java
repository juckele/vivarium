package io.vivarium.scripts;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.audit.AuditType;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;
import io.vivarium.serialization.SerializationEngine;

public class CreateAuditBlueprint extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";
    private static final String AUDIT_TYPE = "type";

    public CreateAuditBlueprint(String[] args)
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
                .desc("file to save to audit blueprint to")
                .build());
        options.add(Option
                .builder("t")
                .required(true)
                .longOpt(AUDIT_TYPE)
                .hasArg(true)
                .argName("TYPE")
                .desc("type of audit blueprint to create")
                .build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        Map<String, Object> extraOptions = this.extraArgsAsMap(commandLine);

        // Determine the correct type
        String auditTypeString = commandLine.getOptionValue(AUDIT_TYPE);
        AuditType auditType = null;
        try
        {
            auditType = AuditType.valueOf(auditTypeString);
        }
        catch (Exception e)
        {
            System.out.println("Unable to decode type " + auditTypeString + ". Legal values are "
                    + Arrays.toString(AuditType.values()));
            return;
        }

        // Build the audit blueprint
        AuditBlueprint auditBlueprint = auditType.makeAuditBlueprint();
        new SerializationEngine().deserialize(auditBlueprint, extraOptions);

        // Save the audit blueprint
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        FileIO.saveSerializer(auditBlueprint, outputFile, Format.JSON);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating audit blueprints.";
    }

    @Override
    protected String getExtraArgString()
    {
        return " [key value [key value ...]]";
    }

    public static void main(String[] args)
    {
        new CreateAuditBlueprint(args);
    }
}
