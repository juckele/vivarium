package com.johnuckele.vivarium.scripts;

public class Harness
{
	public static void main(String[] args)
	{
		String[] args1 = {"data/world_saves/medium.viv", "30"};
		CreateWorldScript.main(args1);

		String[] args2 = {"data/world_saves/medium.viv", "20000", "data/world_saves/medium2.viv"};
		RunSimulationScript.main(args2);

		String[] args3 = {"data/world_saves/medium2.viv"};
		PreviewVivariumFile.main(args3);
	}
}
