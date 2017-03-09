package io.vivarium.visualizer.enums;

public enum MouseClickMode
{
    SELECT_CREATURE(false),
    ADD_FLAMETHROWER(true),
    ADD_FOODGENERATOR(true),
    ADD_WALL(true),
    ADD_WALL_BRUTALLY(true),
    REMOVE_TERRAIN(true),
    REMOVE_ANYTHING(true);

    private final boolean _isPaintbrushMode;

    MouseClickMode(boolean isPaintbrushMode)
    {
        _isPaintbrushMode = isPaintbrushMode;
    }

    public boolean isPaintbrushMode()
    {
        return _isPaintbrushMode;
    }
}