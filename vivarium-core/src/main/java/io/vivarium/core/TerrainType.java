package io.vivarium.core;

public enum TerrainType
{
    FLAME, FLAMETHROWER, FOOD_GENERATOR, WALL;

    public static boolean isPathable(TerrainType terrainType)
    {
        if (terrainType == null)
        {
            return true;
        }
        switch (terrainType)
        {
            case FLAME:
                return true;
            case FLAMETHROWER:
                return false;
            case FOOD_GENERATOR:
                return false;
            case WALL:
                return false;
        }
        throw new Error("Null TerrainType");
    }
}
