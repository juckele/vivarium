package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.johnuckele.vivarium.core.Uckeleoid;

public class ExtractUckeleoidFromWorldTest
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
		Uckeleoid u = ScriptIO.loadUckeleoid("test.uck", Format.JAVA_SERIALIZABLE);
		assertNotNull("Uckeleoid is loaded from file correctly", u);
		assertNotNull("Uckeleoid has brain object correctly reloaded", u.getBrain());
		assertTrue("Uckeleoid has reasonable ID", u.getID() >= 0 && u.getID() < (worldSize-2)*(worldSize-2));
		assertEquals("Uckeleoid should have zero age on fresh creation", u.getAge(), 0);
		// Run for a few hundred ticks
		{
			String[] commandArgs =
			{ "test.viv", "200", "test.viv" };
			RunSimulation.main(commandArgs);
		}
		// Extract the uckeleoid again
		{
			String[] commandArgs =
			{ "test.viv", "test.uck" };
			ExtractUckeleoidFromWorld.main(commandArgs);
		}
		u = ScriptIO.loadUckeleoid("test.uck", Format.JAVA_SERIALIZABLE);
		assertEquals("Uckeleoid age should match age of world", u.getAge(), 200);
	}
}
