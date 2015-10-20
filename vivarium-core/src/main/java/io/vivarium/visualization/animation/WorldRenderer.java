/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization.animation;

import io.vivarium.core.Creature;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;

public abstract class WorldRenderer
{
    public static void renderWorld(GraphicalDelegate graphicalSystem, World w1, World w2, double interpolation,
            int selectedCreatureID)
    {
        terrainRender(graphicalSystem, w1);
        actorRender(graphicalSystem, w1, (int) (System.currentTimeMillis() % 1000), selectedCreatureID);
    }

    private static void terrainRender(GraphicalDelegate graphicalSystem, World w)
    {
        // Draw the exterior walls
        for (int i = 0; i < w.getWorldWidth(); i++)
        {
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, i, 0);
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, i, w.getWorldHeight() - 1);
        }
        for (int i = 0; i < w.getWorldHeight(); i++)
        {
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, 0, i);
            SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, w.getWorldWidth() - 1, i);
        }
        // Draw the floor and interior walls
        for (int i = 1; i < w.getWorldHeight() - 1; i++)
        {
            for (int j = 1; j < w.getWorldWidth() - 1; j++)
            {

                if (w.getEntityType(i, j) == EntityType.WALL)
                {
                    SpriteRenderer.drawSprite(graphicalSystem, Sprite.WALL, j, i);
                }
                else
                {
                    SpriteRenderer.drawSprite(graphicalSystem, Sprite.FLOOR, j, i);
                }

            }
        }
    }

    private static void actorRender(GraphicalDelegate graphicalSystem, World w, int milliseconds,
            int selectedCreatureID)
    {
        // Draw creatures and food
        for (int i = 1; i < w.getWorldHeight() - 1; i++)
        {
            for (int j = 1; j < w.getWorldWidth() - 1; j++)
            {
                if (w.getEntityType(i, j) == EntityType.FOOD)
                {
                    SpriteRenderer.drawSprite(graphicalSystem, Sprite.FOOD, j, i);
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
                        SpriteRenderer.drawSprite(graphicalSystem, creatureSprites, j, i, creature.getFacing());
                    }
                    if (creature.getIsFemale())
                    {
                        if (offsetMilliseconds < 250)
                        {
                            creatureSprites = Sprite.BLUE_CREATURE_1;
                        }
                        else if (offsetMilliseconds < 500)
                        {
                            creatureSprites = Sprite.BLUE_CREATURE_2;
                        }
                        else if (offsetMilliseconds < 750)
                        {
                            creatureSprites = Sprite.BLUE_CREATURE_3;
                        }
                        else
                        {
                            creatureSprites = Sprite.BLUE_CREATURE_2;
                        }
                    }
                    else
                    {
                        if (offsetMilliseconds < 250)
                        {
                            creatureSprites = Sprite.RED_CREATURE_1;
                        }
                        else if (offsetMilliseconds < 500)
                        {
                            creatureSprites = Sprite.RED_CREATURE_2;
                        }
                        else if (offsetMilliseconds < 750)
                        {
                            creatureSprites = Sprite.RED_CREATURE_3;
                        }
                        else
                        {
                            creatureSprites = Sprite.RED_CREATURE_2;
                        }
                    }
                    SpriteRenderer.drawSprite(graphicalSystem, creatureSprites, j, i, creature.getFacing());
                }
            }
        }
    }
}
