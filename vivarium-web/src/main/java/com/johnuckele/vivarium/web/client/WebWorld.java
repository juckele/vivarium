package com.johnuckele.vivarium.web.client;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.EntityType;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldBlueprint;

public class WebWorld extends World
{
    private static final long serialVersionUID = 3L;

    public WebWorld(WorldBlueprint blueprint)
    {
        super(blueprint);
    }

    public void terrainRender(SpriteRenderer renderer)
    {
        // Draw the exterior walls
        for (int i = 0; i < this._worldDimensions; i++)
        {
            renderer.draw(Sprite.WALL, 0, i);
            renderer.draw(Sprite.WALL, this._worldDimensions - 1, i);
            renderer.draw(Sprite.WALL, i, 0);
            renderer.draw(Sprite.WALL, i, this._worldDimensions - 1);
        }
        // Draw the floor and interior walls
        for (int i = 1; i < this._worldDimensions - 1; i++)
        {
            for (int j = 1; j < this._worldDimensions - 1; j++)
            {
                if (this._entityGrid[i][j] == EntityType.WALL)
                {
                    renderer.draw(Sprite.WALL, i, j);
                }
                else
                {
                    renderer.draw(Sprite.FLOOR, i, j);
                }
            }
        }
    }

    public void actorRender(SpriteRenderer renderer, int milliseconds)
    {
        // Draw creatures and food
        for (int i = 1; i < this._worldDimensions - 1; i++)
        {
            for (int j = 1; j < this._worldDimensions - 1; j++)
            {
                if (this._entityGrid[i][j] == EntityType.FOOD)
                {
                    renderer.draw(Sprite.FLOOR, i, j);
                    renderer.draw(Sprite.FOOD, i, j);
                }
                else if (this._entityGrid[i][j] == EntityType.CREATURE)
                {
                    Creature creature = this._creatureGrid[i][j];
                    int offsetMilliseconds = (int) (milliseconds + creature.getRandomSeed() * 1000) % 1000;
                    Sprite creatureSprites;
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
                    renderer.draw(Sprite.FLOOR, i, j);
                    renderer.draw(creatureSprites, i, j, creature.getFacing());
                }
            }
        }
    }
}
