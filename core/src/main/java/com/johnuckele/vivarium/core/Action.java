package com.johnuckele.vivarium.core;

public enum Action
{
    REST, MOVE, TURN_LEFT, TURN_RIGHT, EAT, BREED, BIRTH, DIE;

    public static Action convertIntegerToAction(int i)
    {
        switch (i)
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

    public static int convertActionToInteger(Action a)
    {
        switch (a)
        {
            case REST:
                return 0;
            case MOVE:
                return 1;
            case TURN_LEFT:
                return 2;
            case TURN_RIGHT:
                return 3;
            case EAT:
                return 4;
            case BREED:
                return 5;
            case BIRTH:
                return 6;
            case DIE:
                return 7;
            default:
                throw new Error("Conversion out of bounds error for value " + a);
        }
    }

    public static int getDistinctActionCount()
    {
        return 8;
    }
}
