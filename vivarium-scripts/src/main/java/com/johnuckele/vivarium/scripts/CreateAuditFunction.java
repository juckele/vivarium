package com.johnuckele.vivarium.scripts;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.johnuckele.vivarium.audit.AuditFunction;

public class CreateAuditFunction extends CommonsScript
{
    private static final String OUTPUT_FILE = "output";

    public CreateAuditFunction(String[] args)
    {
        super(args);
    }

    @Override
    protected List<Option> getScriptSpecificOptions()
    {
        LinkedList<Option> options = new LinkedList<Option>();
        options.add(Option.builder("o").required(true).argName("output").longOpt(OUTPUT_FILE).hasArg(true)
                .argName("FILE").desc("file to save to audit function to").build());
        return options;
    }

    @Override
    protected void run(CommandLine commandLine)
    {
        Map<String, Object> extraOptions = this.extraArgsAsMap();

        // Build the audit function
        AuditFunction auditFunction = AuditFunction.makeFromMap(extraOptions);

        // Save the audit function
        String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
        ScriptIO.saveSerializer(auditFunction, outputFile, Format.JSON);
    }

    public static void main(String[] args)
    {
        new CreateAuditFunction(args);
    }

    @Override
    protected String getUsageHeader()
    {
        return "A tool for creating audit functions.";
    }

    @Override
    protected String getExtraArgString()
    {
        return " [key value [key value ...]]";
    }
}
