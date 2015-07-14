package com.johnuckele.vivarium.scripts;

import java.util.Iterator;
import java.util.LinkedList;

import com.johnuckele.vivarium.core.Action;
import com.johnuckele.vivarium.core.ActionProfile;
import com.johnuckele.vivarium.core.Gender;
import com.johnuckele.vivarium.core.World;

public class GenerateGenerationActionProfileCSV extends Script
{
    public GenerateGenerationActionProfileCSV(String[] args)
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

        // Build the CSV data
        StringBuilder csvStringBuilder = new StringBuilder("generation,action,action_success,female,count\n");
        LinkedList<ActionProfile> femaleActionProfiles = w.getAllActionProfilesForGender(Gender.FEMALE);
        LinkedList<ActionProfile> maleActionProfiles = w.getAllActionProfilesForGender(Gender.MALE);

        int generation = 0;
        Iterator<ActionProfile> femaleIterator = femaleActionProfiles.iterator();
        Iterator<ActionProfile> maleIterator = maleActionProfiles.iterator();
        while (femaleIterator.hasNext())
        {
            generation++;
            ActionProfile femaleGenerationActionProfile = femaleIterator.next();
            ActionProfile maleGenerationActionProfile = maleIterator.next();

            // Generate CSV rows
            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < Action.getDistinctActionCount(); j++)
                {
                    for (int k = 0; k < 2; k++)
                    {
                        Action action = Action.convertIntegerToAction(j);
                        boolean actionSuccess = i == 1;
                        boolean isFemale = k == 1;
                        int actionCount;
                        if (isFemale)
                        {
                            actionCount = femaleGenerationActionProfile.getActionCount(action, actionSuccess);

                        }
                        else
                        {
                            actionCount = maleGenerationActionProfile.getActionCount(action, actionSuccess);

                        }
                        csvStringBuilder.append(generation);
                        csvStringBuilder.append(',');
                        csvStringBuilder.append(action);
                        csvStringBuilder.append(',');
                        csvStringBuilder.append(actionSuccess);
                        csvStringBuilder.append(',');
                        csvStringBuilder.append(isFemale);
                        csvStringBuilder.append(',');
                        csvStringBuilder.append(actionCount);
                        csvStringBuilder.append('\n');
                    }
                }
            }
        }

        // Save
        System.out.println("Saving file: " + args[1]);
        ScriptIO.saveStringToFile(csvStringBuilder.toString(), args[1]);
    }

    public static void main(String[] args)
    {
        new GenerateGenerationActionProfileCSV(args);
    }
}
