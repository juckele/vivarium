package com.johnuckele.vivarium.core;

public enum WorldObject
{
	EMPTY, UCKELEOID, FOOD, WALL;

	public static WorldObject parseString(String string)
	{
		String processedString = string.toUpperCase().trim();
		if(processedString == "EMPTY")
		{
			return EMPTY;
		}
		else if(processedString == "UCKELEOID")
		{
			return UCKELEOID;
		}
		else if(processedString == "FOOD")
		{
			return FOOD;
		}
		else if(processedString == "WALL")
		{
			return WALL;
		}
		else
		{
			throw new Error("Invalid parse value "+string);
		}
	}
}
