package com.johnuckele.vivarium.visualization.animation;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.johnuckele.vivarium.core.Direction;

public class SpriteRenderer
{
    private static ImageIcon IMAGE_ICON = new ImageIcon("src/main/resources/sprites.png");
    private static Image IMAGE = IMAGE_ICON.getImage();

    public static int RENDER_PIXEL_BLOCK_SIZE = 32;
    public static int SOURCE_PIXEL_BLOCK_SIZE = 32;
    private static int FLOOR_X = 0 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int FLOOR_Y = 0 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int WALL_X = 1 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int WALL_Y = 0 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int FOOD_X = 1 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int FOOD_Y = 1 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int RED_CREATURE_1_X = 0 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int RED_CREATURE_1_Y = 2 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int RED_CREATURE_2_X = 1 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int RED_CREATURE_2_Y = 2 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int RED_CREATURE_3_X = 2 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int RED_CREATURE_3_Y = 2 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int BLUE_CREATURE_1_X = 0 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int BLUE_CREATURE_1_Y = 3 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int BLUE_CREATURE_2_X = 1 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int BLUE_CREATURE_2_Y = 3 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int BLUE_CREATURE_3_X = 2 * SOURCE_PIXEL_BLOCK_SIZE;
    private static int BLUE_CREATURE_3_Y = 3 * SOURCE_PIXEL_BLOCK_SIZE;

    @SuppressWarnings("serial")
    private static HashMap<Sprite, Integer> SPRITE_TO_X_OFFSET = new HashMap<Sprite, Integer>()
    {
        {
            put(Sprite.FLOOR, FLOOR_X);
            put(Sprite.WALL, WALL_X);
            put(Sprite.FOOD, FOOD_X);
            put(Sprite.RED_CREATURE_1, RED_CREATURE_1_X);
            put(Sprite.RED_CREATURE_2, RED_CREATURE_2_X);
            put(Sprite.RED_CREATURE_3, RED_CREATURE_3_X);
            put(Sprite.BLUE_CREATURE_1, BLUE_CREATURE_1_X);
            put(Sprite.BLUE_CREATURE_2, BLUE_CREATURE_2_X);
            put(Sprite.BLUE_CREATURE_3, BLUE_CREATURE_3_X);
        }
    };

    @SuppressWarnings("serial")
    private static final HashMap<Sprite, Integer> SPRITE_TO_Y_OFFSET = new HashMap<Sprite, Integer>()
    {
        {
            put(Sprite.FLOOR, FLOOR_Y);
            put(Sprite.WALL, WALL_Y);
            put(Sprite.FOOD, FOOD_Y);
            put(Sprite.RED_CREATURE_1, RED_CREATURE_1_Y);
            put(Sprite.RED_CREATURE_2, RED_CREATURE_2_Y);
            put(Sprite.RED_CREATURE_3, RED_CREATURE_3_Y);
            put(Sprite.BLUE_CREATURE_1, BLUE_CREATURE_1_Y);
            put(Sprite.BLUE_CREATURE_2, BLUE_CREATURE_2_Y);
            put(Sprite.BLUE_CREATURE_3, BLUE_CREATURE_3_Y);
        }
    };

    public static void drawSprite(Graphics2D g2, Sprite sprite, int x, int y, ImageObserver observer)
    {
        g2.drawImage(IMAGE, RENDER_PIXEL_BLOCK_SIZE * x, RENDER_PIXEL_BLOCK_SIZE * y,
                RENDER_PIXEL_BLOCK_SIZE * x + RENDER_PIXEL_BLOCK_SIZE,
                RENDER_PIXEL_BLOCK_SIZE * y + RENDER_PIXEL_BLOCK_SIZE, SPRITE_TO_X_OFFSET.get(sprite),
                SPRITE_TO_Y_OFFSET.get(sprite), SPRITE_TO_X_OFFSET.get(sprite) + SOURCE_PIXEL_BLOCK_SIZE,
                SPRITE_TO_Y_OFFSET.get(sprite) + SOURCE_PIXEL_BLOCK_SIZE, observer);

    }

    public static void drawSprite(Graphics2D g2, Sprite sprite, int x, int y, Direction rotation,
            ImageObserver observer)
    {
        g2.rotate(-Direction.getRadiansFromNorth(rotation), (x + 0.5) * RENDER_PIXEL_BLOCK_SIZE,
                (y + 0.5) * RENDER_PIXEL_BLOCK_SIZE);
        g2.drawImage(IMAGE, RENDER_PIXEL_BLOCK_SIZE * x, RENDER_PIXEL_BLOCK_SIZE * y,
                RENDER_PIXEL_BLOCK_SIZE * x + RENDER_PIXEL_BLOCK_SIZE,
                RENDER_PIXEL_BLOCK_SIZE * y + RENDER_PIXEL_BLOCK_SIZE, SPRITE_TO_X_OFFSET.get(sprite),
                SPRITE_TO_Y_OFFSET.get(sprite), SPRITE_TO_X_OFFSET.get(sprite) + SOURCE_PIXEL_BLOCK_SIZE,
                SPRITE_TO_Y_OFFSET.get(sprite) + SOURCE_PIXEL_BLOCK_SIZE, observer);
        g2.rotate(Direction.getRadiansFromNorth(rotation), (x + 0.5) * RENDER_PIXEL_BLOCK_SIZE,
                (y + 0.5) * RENDER_PIXEL_BLOCK_SIZE);

    }
}
