package com.johnuckele.vivarium.web.client;

import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;

public class WebWorld extends World
{
	private static final long	serialVersionUID	= 3L;

	public WebWorld(int worldDimensions, WorldVariables worldVariables)
	{
		super(worldDimensions, worldVariables);
	}

	public void terrainRender(SpriteRenderer renderer)
	{
		// Draw the exterior walls
		for(int i = 0; i < this._worldDimensions; i++)
		{
			renderer.draw(Sprite.WALL, 0, i);
			renderer.draw(Sprite.WALL, this._worldDimensions-1, i);
			renderer.draw(Sprite.WALL, i, 0);
			renderer.draw(Sprite.WALL, i, this._worldDimensions-1);
		}
		// Draw the floor and interior walls
		for(int i = 1; i < this._worldDimensions-1; i++)
		{
			for(int j = 1; j < this._worldDimensions-1; j++)
			{
				if(this._worldObjectGrid[i][j] == WorldObject.WALL)
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
		// Draw uckeleoids and food
		for(int i = 1; i < this._worldDimensions-1; i++)
		{
			for(int j = 1; j < this._worldDimensions-1; j++)
			{
				if(this._worldObjectGrid[i][j] == WorldObject.FOOD)
				{
					renderer.draw(Sprite.FLOOR, i, j);
					renderer.draw(Sprite.FOOD, i, j);
				}
				else if(this._worldObjectGrid[i][j] == WorldObject.UCKELEOID)
				{
					Uckeleoid uckeleoid = this._uckeleoidGrid[i][j];
					int offsetMilliseconds = (int)(milliseconds + uckeleoid.getRandomSeed() * 1000) % 1000;
					Sprite uckeleoidSprite;
					if(uckeleoid.getIsFemale())
					{
						if(offsetMilliseconds < 250)
						{
							uckeleoidSprite = Sprite.BLUE_UCKELEOID_1;
						}
						else if(offsetMilliseconds < 500)
						{
							uckeleoidSprite = Sprite.BLUE_UCKELEOID_2;
						}
						else if(offsetMilliseconds < 750)
						{
							uckeleoidSprite = Sprite.BLUE_UCKELEOID_3;
						}
						else
						{
							uckeleoidSprite = Sprite.BLUE_UCKELEOID_2;
						}
					}
					else
					{
						if(offsetMilliseconds < 250)
						{
							uckeleoidSprite = Sprite.RED_UCKELEOID_1;
						}
						else if(offsetMilliseconds < 500)
						{
							uckeleoidSprite = Sprite.RED_UCKELEOID_2;
						}
						else if(offsetMilliseconds < 750)
						{
							uckeleoidSprite = Sprite.RED_UCKELEOID_3;
						}
						else
						{
							uckeleoidSprite = Sprite.RED_UCKELEOID_2;
						}
					}
					renderer.draw(Sprite.FLOOR, i, j);
					renderer.draw(uckeleoidSprite, i, j, uckeleoid.getFacing());
				}
			}
		}
	}
}
