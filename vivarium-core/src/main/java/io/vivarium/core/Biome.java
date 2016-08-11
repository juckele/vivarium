package io.vivarium.core;

public class Biome
{
    public enum DoorDirection
    {
        NORTH, EAST, SOUTH, WEST;
    }

    public Biome()
    {

    }

    public void plain(World w, int startR, int startC, int width, int height)
    {
        forest(w, 0, startR, startC, width, height);
    }

    public void house(World w, int startR, int startC, int width, int height, DoorDirection dir)
    {
        for (int r = startR; r < startR + height; r++)
        {
            for (int c = startC; c < startC + width; c++)
            {

                if (r < startR + 1 || c < startC + 1 || r > startR + height - 2 || c > startC + width - 2)
                {
                    w.setTerrain(TerrainType.WALL, r, c);
                }
            }
        }

        if (dir == DoorDirection.NORTH)
        {
            w.setTerrain(null, startR, startC + width / 2);
        }
        if (dir == DoorDirection.SOUTH)
        {
            w.setTerrain(null, startR + height - 1, startC + width / 2);
        }
        if (dir == DoorDirection.EAST)
        {
            w.setTerrain(null, startR + height / 2, startC + width - 1);
        }
        if (dir == DoorDirection.WEST)
        {
            w.setTerrain(null, startR + height / 2, startC);
        }
    }

    public void forest(World w, double density, int startR, int startC, int width, int height)
    {

        int densityHFactor = (int) (1.0 - density) * height;
        int densityWFactor = (int) (1.0 - density) * width;

        if (densityHFactor == 0)
        {
            densityHFactor = 1;
        }
        if (densityWFactor == 0)
        {
            densityWFactor = 1;
        }

        if (density == 0)
        {
            return;
        }

        for (int r = startR; r < height; r++)
        {
            for (int c = startC; c < width; c++)
            {
                if (r % densityHFactor == 0 && c % densityWFactor == 0)
                {
                    w.setTerrain(TerrainType.WALL, r, c);
                }
            }
        }
    }
}
