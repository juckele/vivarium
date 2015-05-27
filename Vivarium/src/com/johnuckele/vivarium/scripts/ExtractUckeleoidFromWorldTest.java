package com.johnuckele.vivarium.scripts;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.johnuckele.vivarium.core.Uckeleoid;

public class ExtractUckeleoidFromWorldTest
{
	@Test public void test()
	{
		// Create a world
		{
			String[] commandArgs =
			{ "test.viv", "25" };
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
	}
}
