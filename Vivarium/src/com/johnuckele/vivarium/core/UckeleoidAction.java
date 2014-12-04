package com.johnuckele.vivarium.core;

public enum UckeleoidAction
{
	REST, MOVE, TURN_LEFT, TURN_RIGHT, EAT, BREED, BIRTH, DIE;

	public static UckeleoidAction convertIntegerToAction(int i)
	{
		switch ( i )
		{
			case 0:
				return REST;
			case 1:
				return MOVE;
			case 2:
				return TURN_LEFT;
			case 3:
				return TURN_RIGHT;
			case 4:
				return EAT;
			case 5:
				return BREED;
			case 6:
				return BIRTH;
			case 7:
				return DIE;
			default:
				throw new Error("Conversion out of bounds error for value " + i);
		}
	}
}
