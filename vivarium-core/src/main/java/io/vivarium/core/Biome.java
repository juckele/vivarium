package io.vivarium.core;

public class Biome
{
    public enum DoorDirection
    {
        TOP, DOWN, LEFT, RIGHT;
    }

    public Biome()
    {

    }

    public void plain(World w, int startR, int startC, int width, int height)
    {
        forest(w, 0, startR, startC, width, height);
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

        if (dir == DoorDirection.TOP)
        {
            w.setTerrain(null, startR, startC + width / 2);
        }
        if (dir == DoorDirection.DOWN)
        {
            w.setTerrain(null, startR + height - 1, startC + width / 2);
        }
        if (dir == DoorDirection.RIGHT)
        {
            w.setTerrain(null, startR + height / 2, startC + width - 1);
        }
        if (dir == DoorDirection.LEFT)
        {
            w.setTerrain(null, startR + height / 2, startC);
        }
    }

    // assuming square villages of area size*size
    public void village(World w, int houses, int width, int height, int startR, int startC, int pathSize)
    {
        // find closest perfect square >= number of houses
        int square = (int) Math.sqrt(houses);
        if (square * square != houses)
        {
            square = (int) Math.sqrt(houses) + 1;
        }

        int plotWidth = width / square;
        int plotHeight = height / square;
        int houseWidth = plotWidth - pathSize;
        int houseHeight = plotHeight - pathSize;

        // empty plots
        int totalPlots = square * square;
        int emptyPlots = totalPlots - houses;
        int distribution;
        if (emptyPlots == 0)
        {
            distribution = totalPlots + 1;
        }
        else
        {
            distribution = totalPlots / emptyPlots;
        }

        int plotCounter = 0;

        for (int r = 0; r < square; r++)
        {
            for (int c = 0; c < square; c++)
            {
                plotCounter++;
                if (plotCounter % distribution != 0)
                {
                    house(w, startR + r * plotHeight, startC + c * plotWidth, houseWidth, houseHeight,
                            DoorDirection.RIGHT);
                }

            }
        }
    }
}
