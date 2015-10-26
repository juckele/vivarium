/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.scripts;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class CommonsScript
{
    public CommonsScript(String[] args)
    {
        runAsScript(args);
    }

    public CommonsScript()
    {
    }

    public void runAsScript(String[] args)
    {
        CommandLine commandLine = parseArgs(args);
        if (commandLine != null)
        {
            run(commandLine);
        }
    }

    protected CommandLine parseArgs(String[] args)
    {
        DefaultParser parser = new DefaultParser();
        try
        {
            CommandLine commandLine = parser.parse(this.getOptions(), args);
            if (commandLine.hasOption("help"))
            {
                printUsageAndExit();
            }
            else
            {
                return commandLine;
            }
        }
        catch (ParseException e)
        {
            printUsageAndExit();
        }
        return null;
    }

    protected Map<String, Object> extraArgsAsMap(CommandLine commandLine)
    {
        String[] args = commandLine.getArgs();
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i + 1 < args.length; i += 2)
        {
            map.put(args[i], args[i + 1]);
        }
        return map;
    }

    protected Options getOptions()
    {
        Options options = new Options();
        for (Option option : this.getCommonOptions())
        {
            options.addOption(option);
        }
        for (Option option : this.getScriptSpecificOptions())
        {
            options.addOption(option);
        }
        return options;
    }

    protected Options getRequiredOptions()
    {
        Options requiredOptions = new Options();
        for (Option option : this.getCommonOptions())
        {
            if (option.isRequired())
            {
                requiredOptions.addOption(option);
            }
        }
        for (Option option : this.getScriptSpecificOptions())
        {
            if (option.isRequired())
            {
                requiredOptions.addOption(option);
            }
        }
        return requiredOptions;
    }

    protected abstract List<Option> getScriptSpecificOptions();

    protected List<Option> getCommonOptions()
    {
        LinkedList<Option> commonOptions = new LinkedList<Option>();
        commonOptions.add(Option.builder("h").required(false).longOpt("help").hasArg(false)
                .desc("display this help and exit").build());
        return commonOptions;
    }

    protected abstract String getExtraArgString();

    protected abstract String getUsageHeader();

    protected String getUsageFooter()
    {
        return "";
    }

    protected abstract void run(CommandLine commandLine);

    protected void printUsageAndExit()
    {
        // Pull out just the required options
        Options requiredOptions = this.getRequiredOptions();
        StringWriter requiredOptionsSyntax = new StringWriter();
        PrintWriter requiredOptionPrinter = new PrintWriter(requiredOptionsSyntax);
        HelpFormatter syntaxFormatter = new HelpFormatter();
        syntaxFormatter.printUsage(requiredOptionPrinter, 120, "", requiredOptions);
        String requiredOptionsString = requiredOptionsSyntax.toString().trim();

        // Do the rest of the stuff
        Options fullOptions = this.getOptions();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(100,
                this.getClass().getSimpleName() + requiredOptionsString + " [additional options]" + getExtraArgString(),
                this.getUsageHeader(), fullOptions, this.getUsageFooter());
    }
}
