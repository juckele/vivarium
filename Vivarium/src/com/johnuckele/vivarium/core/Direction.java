package com.johnuckele.vivarium.core;

public enum Direction
{
	NORTH, EAST, SOUTH, WEST;

	public static int getHorizontalComponent(Direction direction)
	{
		switch ( direction )
		{
			case EAST:
				return (1);
			case WEST:
				return (-1);
			case NORTH:
			case SOUTH:
				return (0);
		}
		throw new Error("Null Direction");
	}

	public static int getVerticalComponent(Direction direction)
	{
		switch ( direction )
		{
			case NORTH:
				return (-1);
			case SOUTH:
				return (1);
			case EAST:
			case WEST:
				return (0);
		}
		throw new Error("Null Direction");
	}

	public static Direction stepCounterclockwise(Direction direction)
	{
		switch ( direction )
		{
			case NORTH:
				return (EAST);
			case EAST:
				return (SOUTH);
			case SOUTH:
				return (WEST);
			case WEST:
				return (NORTH);
		}
		return null;
	}

	public static Direction stepClockwise(Direction direction)
	{
		switch ( direction )
		{
			case NORTH:
				return (WEST);
			case EAST:
				return (NORTH);
			case SOUTH:
				return (EAST);
			case WEST:
				return (SOUTH);
		}
		return null;
	}

	public static Direction flipDirection(Direction direction)
	{
		switch ( direction )
		{
			case NORTH:
				return (SOUTH);
			case EAST:
				return (WEST);
			case SOUTH:
				return (NORTH);
			case WEST:
				return (EAST);
		}
		return null;
	}
}
