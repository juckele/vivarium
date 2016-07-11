package io.vivarium.visualization.animation;

import io.vivarium.core.Creature;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;

public abstract class WorldRenderer
{
    private static final int DEFAULT_COLOR = 0;

    public static void renderWorld(WorldGraphics graphicalSystem, World w1, World w2, double interpolation,
            int selectedCreatureID)
    {
        terrainRender(graphicalSystem, w1);
        actorRender(graphicalSystem, w1, (int) (System.currentTimeMillis() % 1000), selectedCreatureID);
    }

    private static void terrainRender(WorldGraphics graphicalSystem, World w)
    {
        // Draw the exterior walls
        for (int i = 0; i < w.getWorldWidth(); i++)
        {
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, DEFAULT_COLOR, i, 0);
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, DEFAULT_COLOR, i, w.getWorldHeight() - 1);
        }
        for (int i = 0; i < w.getWorldHeight(); i++)
        {
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, DEFAULT_COLOR, 0, i);
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, DEFAULT_COLOR, w.getWorldWidth() - 1, i);
        }
        // Draw the floor and interior walls
        for (int i = 1; i < w.getWorldHeight() - 1; i++)
        {
            for (int j = 1; j < w.getWorldWidth() - 1; j++)
            {

                if (w.getEntityType(i, j) == EntityType.WALL)
                {
                    SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, DEFAULT_COLOR, j, i);
                }
                else
                {
                    SpriteRenderer.drawSprite(graphicalSystem, Sprite.FLOOR, DEFAULT_COLOR, j, i);
                }

            }
        }
    }

    private static void actorRender(WorldGraphics graphicalSystem, World w, int milliseconds, int selectedCreatureID)
    {
        // Draw creatures and food
        for (int i = 1; i < w.getWorldHeight() - 1; i++)
        {
            for (int j = 1; j < w.getWorldWidth() - 1; j++)
            {
                if (w.getEntityType(i, j) == EntityType.FOOD)
                {
                    SpriteRenderer.drawSprite(graphicalSystem, Sprite.FOOD, DEFAULT_COLOR, j, i);
                }
                else if (w.getEntityType(i, j) == EntityType.CREATURE)
                {
                    Creature creature = w.getCreature(i, j);
                    int offsetMilliseconds = (int) (milliseconds + creature.getRandomSeed() * 1000) % 1000;
                    Sprite creatureSprites;
                    if (creature.getID() == selectedCreatureID)
                    {
                        if (offsetMilliseconds < 250)
                        {
                            creatureSprites = Sprite.HALO_CREATURE_1;
                        }
                        else if (offsetMilliseconds < 500)
                        {
                            creatureSprites = Sprite.HALO_CREATURE_2;
                        }
                        else if (offsetMilliseconds < 750)
                        {
                            creatureSprites = Sprite.HALO_CREATURE_3;
                        }
                        else
                        {
                            creatureSprites = Sprite.HALO_CREATURE_2;
                        }
                        SpriteRenderer.drawSprite(graphicalSystem, creatureSprites, DEFAULT_COLOR, j, i,
                                creature.getFacing());
                    }

                    if (offsetMilliseconds < 250)
                    {
                        creatureSprites = Sprite.CREATURE_1;
                    }
                    else if (offsetMilliseconds < 500)
                    {
                        creatureSprites = Sprite.CREATURE_2;
                    }
                    else if (offsetMilliseconds < 750)
                    {
                        creatureSprites = Sprite.CREATURE_3;
                    }
                    else
                    {
                        creatureSprites = Sprite.CREATURE_2;
                    }

                    int creatureColor;
                    if (creature.getIsFemale())
                    {
                        if (creature.getGestation() != 0)
                        {
                            creatureColor = 2;
                        }
                        else
                        {
                            creatureColor = 1;
                        }
                    }
                    else
                    {
                        creatureColor = 0;
                    }
                    SpriteRenderer.drawSprite(graphicalSystem, creatureSprites, creatureColor, j, i,
                            creature.getFacing());
                }
            }
        }
    }
}
