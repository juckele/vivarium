package com.johnuckele.vivarium.visualization.animation;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.World;

public class WorldRenderer
{

    public static void terrainRender(Graphics2D g2, World w, ImageObserver observer)
    {
        // Draw the exterior walls
        for (int i = 0; i < w.getWorldDimensions(); i++)
        {
            SpriteRenderer.drawSprite(g2, Sprite.WALL, i, 0, observer);
            SpriteRenderer.drawSprite(g2, Sprite.WALL, i, w.getWorldDimensions() - 1, observer);
            SpriteRenderer.drawSprite(g2, Sprite.WALL, 0, i, observer);
            SpriteRenderer.drawSprite(g2, Sprite.WALL, w.getWorldDimensions() - 1, i, observer);
        }
        // Draw the floor and interior walls
        for (int i = 1; i < w.getWorldDimensions() - 1; i++)
        {
            for (int j = 1; j < w.getWorldDimensions() - 1; j++)
            {

                if (w.getEntityType(i, j) == EntityType.WALL)
                {
                    SpriteRenderer.drawSprite(g2, Sprite.WALL, j, i, observer);
                }
                else
                {
                    SpriteRenderer.drawSprite(g2, Sprite.FLOOR, j, i, observer);
                }

            }
        }
    }

    public static void actorRender(Graphics2D g2, World w, ImageObserver observer, int milliseconds)
    {
        // Draw creatures and food
        for (int i = 1; i < w.getWorldDimensions() - 1; i++)
        {
            for (int j = 1; j < w.getWorldDimensions() - 1; j++)
            {
                if (w.getEntityType(i, j) == EntityType.FOOD)
                {
                    SpriteRenderer.drawSprite(g2, Sprite.FOOD, j, i, observer);
                }
                else if (w.getEntityType(i, j) == EntityType.CREATURE)
                {
                    Creature creature = w.getCreature(i, j);
                    int offsetMilliseconds = (int) (milliseconds + creature.getRandomSeed() * 1000) % 1000;
                    Sprite creatureSprites;
                    if (creature.getID() == 42)
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
                        SpriteRenderer.drawSprite(g2, creatureSprites, j, i, creature.getFacing(), observer);
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
                    SpriteRenderer.drawSprite(g2, creatureSprites, j, i, creature.getFacing(), observer);
                }
            }
        }
    }

}
