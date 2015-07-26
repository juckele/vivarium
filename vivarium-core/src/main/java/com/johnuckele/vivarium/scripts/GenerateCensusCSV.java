package com.johnuckele.vivarium.scripts;

import java.util.Iterator;

import com.johnuckele.vivarium.core.CensusRecord;
import com.johnuckele.vivarium.core.IntTuple;
import com.johnuckele.vivarium.core.World;

public class GenerateCensusCSV extends Script
{
    public GenerateCensusCSV(String[] args)
    {
        super(args);
    }

    @Override
    protected boolean argumentCountIsValid(int argCount)
    {
        if (argCount == 2)
        {
            return true;
        }
        return false;
    }

    @Override
    protected String getUsage()
    {
        return "Usage: java scriptPath worldInputFilePath csvOutputFilePath";
    }

    @Override
    protected void run(String[] args)
    {
        // Setup
        World w = ScriptIO.loadWorld(args[0], Format.JAVA_SERIALIZABLE);
        System.out.println("Fix Census " + w);// TODO FIX CENSUS
        CensusRecord census = new CensusRecord(0);// w.getCensus();

        // Build the CSV data
        StringBuilder csvStringBuilder = new StringBuilder("tick,population\n");
        Iterator<IntTuple> it = census.getRecords().iterator();
        while (it.hasNext())
        {
            IntTuple record = it.next();
            csvStringBuilder.append(record.a);
            csvStringBuilder.append(',');
            csvStringBuilder.append(record.b);
            csvStringBuilder.append('\n');
        }

        // Save
        System.out.println("Saving file: " + args[1]);
        ScriptIO.saveStringToFile(csvStringBuilder.toString(), args[1]);
    }

    public static void main(String[] args)
    {
        new GenerateCensusCSV(args);
    }
}
