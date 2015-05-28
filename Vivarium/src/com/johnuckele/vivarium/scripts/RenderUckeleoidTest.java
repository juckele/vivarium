package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RenderUckeleoidTest
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
		// Extract the uckeleoid
		{
			String[] commandArgs =
			{ "test.viv", "test.uck" };
			ExtractUckeleoidFromWorld.main(commandArgs);
		}
		// Render the uckeleoid
		{
			String[] commandArgs =
			{ "test.uck" };
			RenderUckeleoid.main(commandArgs);
		}
		assertTrue("Code did not crash", true);
	}
}
