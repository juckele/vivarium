package com.johnuckele.vivarium.scripts;

import java.util.Iterator;

import com.johnuckele.vivarium.core.ActionHistory;
import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.core.UckeleoidAction;
import com.johnuckele.vivarium.core.World;

public class GenerateGenerationActionProfileCSV extends Script
{
	public GenerateGenerationActionProfileCSV(String[] args)
	{
		super(args);
	}

	@Override protected boolean argumentCountIsValid(int argCount)
	{
		if(argCount == 2)
		{
			return true;
		}
		return false;
	}

	@Override protected String getUsage()
	{
		return "Usage: java scriptPath worldInputFilePath csvOutputFilePath";
	}

	@Override protected void run(String[] args)
	{
		// Setup
		World w = loadWorld(args[0], Format.JAVA_SERIALIZABLE);

		// Build the CSV data
		StringBuilder csvStringBuilder = new StringBuilder("generation,action,action_success,female,count\n");
		int generation = 1;
		ActionHistory femaleGenerationActionHistory = new ActionHistory();
		ActionHistory maleGenerationActionHistory = new ActionHistory();
		ActionHistory uckeleoidActionHistory;
		Iterator<Uckeleoid> it = w.getAllUckeleoids().iterator();
		while(it.hasNext())
		{
			Uckeleoid u = it.next();
			uckeleoidActionHistory = u.getActionHistory();

			// If this uckeleoid is part of the next generation
			// write the action history for the last generation and start a new
			// one
			if((int) u.getGeneration() > generation)
			{
				// Generate CSV row
				for(int i = 0; i < 2; i++)
				{
					for(int j = 0; j < UckeleoidAction.getDistinctActionCount(); j++)
					{
						for(int k = 0; k < 2; k++)
						{
						UckeleoidAction action = UckeleoidAction.convertIntegerToAction(j);
						boolean actionSuccess = i == 1;
						boolean isFemale = k == 1;
						int actionCount;
						if(isFemale)
						{
							 actionCount = femaleGenerationActionHistory.getActionCount(action, actionSuccess);
							
						}
						else
						{
							 actionCount = maleGenerationActionHistory.getActionCount(action, actionSuccess);
							
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
				// Start new generation data
				generation = (int)u.getGeneration();
				femaleGenerationActionHistory = new ActionHistory();
				maleGenerationActionHistory = new ActionHistory();
				if(u.getIsFemale())
				{
					femaleGenerationActionHistory.addActionHistory(uckeleoidActionHistory);
				}
				else
				{
					maleGenerationActionHistory.addActionHistory(uckeleoidActionHistory);
				}
			}
			// Otherwise, just append this uckeleoid's action history to their
			// generation's
			else
			{
				if(u.getIsFemale())
				{
					femaleGenerationActionHistory.addActionHistory(uckeleoidActionHistory);
				}
				else
				{
					maleGenerationActionHistory.addActionHistory(uckeleoidActionHistory);
				}
			}

		}

		// Save
		System.out.println("Saving file: " + args[1]);
		Script.saveStringToFile(csvStringBuilder.toString(), args[1]);
	}

	public static void main(String[] args)
	{
		new GenerateGenerationActionProfileCSV(args);
	}
}
