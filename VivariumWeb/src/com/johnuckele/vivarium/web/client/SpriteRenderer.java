package com.johnuckele.vivarium.web.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.johnuckele.vivarium.core.Direction;

public class SpriteRenderer
{
	public static int		PIXEL_BLOCK_SIZE	= 32;
	private static int		FLOOR_X				= 0 * PIXEL_BLOCK_SIZE;
	private static int		FLOOR_Y				= 0 * PIXEL_BLOCK_SIZE;
	private static int		WALL_X				= 1 * PIXEL_BLOCK_SIZE;
	private static int		WALL_Y				= 0 * PIXEL_BLOCK_SIZE;
	private static int		FOOD_X				= 1 * PIXEL_BLOCK_SIZE;
	private static int		FOOD_Y				= 1 * PIXEL_BLOCK_SIZE;
	private static int		RED_CREATURE_1_X	= 0 * PIXEL_BLOCK_SIZE;
	private static int		RED_CREATURE_1_Y	= 2 * PIXEL_BLOCK_SIZE;
	private static int		RED_CREATURE_2_X	= 1 * PIXEL_BLOCK_SIZE;
	private static int		RED_CREATURE_2_Y	= 2 * PIXEL_BLOCK_SIZE;
	private static int		RED_CREATURE_3_X	= 2 * PIXEL_BLOCK_SIZE;
	private static int		RED_CREATURE_3_Y	= 2 * PIXEL_BLOCK_SIZE;
	private static int		BLUE_CREATURE_1_X	= 0 * PIXEL_BLOCK_SIZE;
	private static int		BLUE_CREATURE_1_Y	= 3 * PIXEL_BLOCK_SIZE;
	private static int		BLUE_CREATURE_2_X	= 1 * PIXEL_BLOCK_SIZE;
	private static int		BLUE_CREATURE_2_Y	= 3 * PIXEL_BLOCK_SIZE;
	private static int		BLUE_CREATURE_3_X	= 2 * PIXEL_BLOCK_SIZE;
	private static int		BLUE_CREATURE_3_Y	= 3 * PIXEL_BLOCK_SIZE;

	private Context2d		_context;
	private ImageElement	_imageElement;

	public SpriteRenderer(Context2d context, ImageElement imageElement)
	{
		this._context = context;
		this._imageElement = imageElement;
	}

	public void draw(Sprite sprite, int r, int c)
	{
		this.draw(sprite, r, c, Direction.NORTH);
	}

	public void draw(Sprite sprite, int r, int c, Direction d)
	{
		// Pick the correct source location for the sprite
		int sourceX = 0, sourceY = 0;
		if(sprite == Sprite.FLOOR)
		{
			sourceX = FLOOR_X;
			sourceY = FLOOR_Y;
		}
		else if(sprite == Sprite.WALL)
		{
			sourceX = WALL_X;
			sourceY = WALL_Y;
		}
		else if(sprite == Sprite.FOOD)
		{
			sourceX = FOOD_X;
			sourceY = FOOD_Y;
		}
		else if(sprite == Sprite.RED_CREATURE_1)
		{
			sourceX = RED_CREATURE_1_X;
			sourceY = RED_CREATURE_1_Y;
		}
		else if(sprite == Sprite.RED_CREATURE_2)
		{
			sourceX = RED_CREATURE_2_X;
			sourceY = RED_CREATURE_2_Y;
		}
		else if(sprite == Sprite.RED_CREATURE_3)
		{
			sourceX = RED_CREATURE_3_X;
			sourceY = RED_CREATURE_3_Y;
		}
		else if(sprite == Sprite.BLUE_CREATURE_1)
		{
			sourceX = BLUE_CREATURE_1_X;
			sourceY = BLUE_CREATURE_1_Y;
		}
		else if(sprite == Sprite.BLUE_CREATURE_2)
		{
			sourceX = BLUE_CREATURE_2_X;
			sourceY = BLUE_CREATURE_2_Y;
		}
		else if(sprite == Sprite.BLUE_CREATURE_3)
		{
			sourceX = BLUE_CREATURE_3_X;
			sourceY = BLUE_CREATURE_3_Y;
		}
		// Translate and rotate
		_context.translate((c + 0.5) * PIXEL_BLOCK_SIZE, (r + 0.5) * PIXEL_BLOCK_SIZE);
		if(d == Direction.NORTH)
		{
			_context.rotate(0);
		}
		else if(d == Direction.EAST)
		{
			_context.rotate(Math.PI / 2);
		}
		else if(d == Direction.SOUTH)
		{
			_context.rotate(Math.PI);
		}
		else if(d == Direction.WEST)
		{
			_context.rotate(-Math.PI / 2);
		}

		// Render the sprite
		_context.drawImage(_imageElement, sourceX, sourceY, PIXEL_BLOCK_SIZE, PIXEL_BLOCK_SIZE, -0.5 * PIXEL_BLOCK_SIZE, -0.5 * PIXEL_BLOCK_SIZE, PIXEL_BLOCK_SIZE, PIXEL_BLOCK_SIZE);

		// Untranslate
		if(d == Direction.NORTH)
		{
			_context.rotate(0);
		}
		else if(d == Direction.EAST)
		{
			_context.rotate(-Math.PI / 2);
		}
		else if(d == Direction.SOUTH)
		{
			_context.rotate(-Math.PI);
		}
		else if(d == Direction.WEST)
		{
			_context.rotate(Math.PI / 2);
		}
		_context.translate(-(c + 0.5) * PIXEL_BLOCK_SIZE, -(r + 0.5) * PIXEL_BLOCK_SIZE);

	}
}
