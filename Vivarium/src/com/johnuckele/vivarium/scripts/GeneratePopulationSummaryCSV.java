package com.johnuckele.vivarium.scripts;

import java.util.Iterator;

import com.johnuckele.vivarium.core.CensusRecord;
import com.johnuckele.vivarium.core.IntTuple;
import com.johnuckele.vivarium.core.World;

public class GeneratePopulationSummaryCSV extends Script
{
	public GeneratePopulationSummaryCSV(String[] args)
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
		CensusRecord census = w.getCensus();

		// Build the CSV data
		StringBuilder csvStringBuilder = new StringBuilder("unique_population,min_population,max_population,mean_population\n");
		int minPopulation = 0;
		int maxPopulation = 0;
		double meanPopulation = 0;
		long totalPopulationTicks = 0;
		int lastPopulationRecord = 0;
		int lastTickRecord = 0;
		Iterator<IntTuple> it = census.getRecords().iterator();
		if(it.hasNext())
		{
			IntTuple record = it.next();
			minPopulation = record.b;
			maxPopulation = record.b;
			totalPopulationTicks += (record.a - lastTickRecord) * lastPopulationRecord;
			lastPopulationRecord = record.b;
			lastTickRecord = record.a;
		}
		while(it.hasNext())
		{
			IntTuple record = it.next();
			if(record.b > maxPopulation)
			{
				maxPopulation = record.b;
			}
			if(record.b < minPopulation)
			{
				minPopulation = record.b;
			}
			totalPopulationTicks += (record.a - lastTickRecord) * lastPopulationRecord;
			lastPopulationRecord = record.b;
			lastTickRecord = record.a;
		}
		// Unique population
		csvStringBuilder.append(w.getMaximumUckeleoidID());
		csvStringBuilder.append(',');
		// minimum population
		csvStringBuilder.append(minPopulation);
		csvStringBuilder.append(',');
		// maximum population
		csvStringBuilder.append(maxPopulation);
		csvStringBuilder.append(',');
		// mean population
		totalPopulationTicks += (w.getTickCounter() - lastTickRecord) * lastPopulationRecord;
		meanPopulation = totalPopulationTicks / (double)w.getTickCounter();
		csvStringBuilder.append(meanPopulation);
		csvStringBuilder.append('\n');

		// Save
		System.out.println("Saving file: "+args[1]);
		Script.saveStringToFile(csvStringBuilder.toString(), args[1]);
	}

	public static void main(String[] args)
	{
		new GeneratePopulationSummaryCSV(args);
	}
}
