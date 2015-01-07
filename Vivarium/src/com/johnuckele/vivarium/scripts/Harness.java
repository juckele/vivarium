package com.johnuckele.vivarium.scripts;

public class Harness
{
	public static void main(String[] args)
	{
		{
			String[] commandArgs =
			{ "data/world_saves/medium.viv", "25" };
			CreateWorldScript.main(commandArgs);

		}

		{
			String[] commandArgs =
			{ "data/world_saves/medium.viv", "200000", "data/world_saves/medium2.viv" };
			RunSimulationScript.main(commandArgs);
		}
		
		{
			String[] commandArgs =
			{ "data/world_saves/medium2.viv", "data/csv/census.csv" };
			GenerateCensusCSV.main(commandArgs);	
		}
		
	}

	public static void inactive()
	{
		for(int i = 3; i < 10; i++)
		{
			System.out.println("Loading "+i+" and saving to "+(i+1));
			String[] commandArgs =
			{ "data/world_saves/medium"+i+".viv", "200000", "data/world_saves/medium"+(i+1)+".viv" };
			RunSimulationScript.main(commandArgs);
		}
		{
			String[] commandArgs =
			{ "data/world_saves/world_viewer/tick0.viv", "data/world_saves/snap1.json" };
			ConvertToJSON.main(commandArgs);
		}
		{
			String[] commandArgs =
			{ "data/world_saves/world_viewer/tick0.viv"};
			PreviewVivariumFile.main(commandArgs);	
		}
		{
			String[] commandArgs =
			{ "data/world_saves/medium2.viv", "data/world_saves/antisocial.json" };
			ConvertToJSON.main(commandArgs);
		}


	}
}
