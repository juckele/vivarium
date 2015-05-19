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

		// Header, this CSV is always a single row so we build the header with the single row
		StringBuilder csvStringBuilder = new StringBuilder("unique_population,min_population,max_population,mean_population,population_stddev\n");

		// Variable declaration
		int minPopulation = 0;
		int maxPopulation = 0;
		double meanPopulation = 0;
		double sumOfSquaredErrors = 0;
		double standardDeviation = 0;
		long totalPopulationTicks = 0;
		int lastPopulationRecord = 0;
		int lastTickRecord = 0;

		// Iterate through and count total population ticks and look for min/max values
		Iterator<IntTuple> it = census.getRecords().iterator();
		if(it.hasNext())
		{
			// This is the initial population and always has time zero
			IntTuple record = it.next();
			minPopulation = record.b;
			maxPopulation = record.b;
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

		// Start building data row

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
		csvStringBuilder.append(',');
		// calculate population standard deviation
		it = census.getRecords().iterator();
		if(it.hasNext())
		{
			// This is the initial population and always has time zero
			IntTuple record = it.next();
			lastPopulationRecord = record.b;
			lastTickRecord = record.a;
		}
		while(it.hasNext())
		{
			IntTuple record = it.next();
			sumOfSquaredErrors += (record.a - lastTickRecord) * Math.pow(lastPopulationRecord - meanPopulation, 2);
			lastPopulationRecord = record.b;
			lastTickRecord = record.a;
		}
		sumOfSquaredErrors += (w.getTickCounter() - lastTickRecord) * Math.pow(lastPopulationRecord - meanPopulation, 2);
		standardDeviation = Math.sqrt(sumOfSquaredErrors / (double)w.getTickCounter()); 
		csvStringBuilder.append(standardDeviation);		
		// end the line
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
