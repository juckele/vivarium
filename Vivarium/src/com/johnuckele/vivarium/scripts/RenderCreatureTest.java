package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RenderCreatureTest
{
	@Test public void test()
	{
		int worldSize = 10;
		// Create a world
		{
			String[] commandArgs =
			{ "test.viv", ""+worldSize };
			CreateWorld.main(commandArgs);
		}
		// Extract the creature
		{
			String[] commandArgs =
			{ "test.viv", "test.uck" };
			ExtractCreatureFromWorld.main(commandArgs);
		}
		// Render the creature
		{
			String[] commandArgs =
			{ "test.uck" };
			RenderCreature.main(commandArgs);
		}
		assertTrue("Code did not crash", true);
	}
}
