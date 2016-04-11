package io.vivarium.visualizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.vivarium.core.Creature;
import io.vivarium.core.Direction;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;

public class Vivarium extends ApplicationAdapter
{
    private static final int SIZE = 30;
    private static final int BLOCK_SIZE = 32;
    // Simulation information
    private WorldBlueprint _blueprint;
    private World _world;
    private int tick = 0;

    // Low Level Graphics information
    private SpriteBatch _batch;
    private Texture _img;
    private Sprite _colored;

    @Override
    public void create()
    {
        _blueprint = WorldBlueprint.makeDefault();
        _blueprint.setSize(SIZE);
        _world = new World(_blueprint);

        _batch = new SpriteBatch();
        _img = new Texture("sprites.png");
        _colored = new Sprite(_img);
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _colored.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1f);
        _colored.setPosition(100, 00);
        _batch.begin();
        // batch.draw(img, 0, 0);

        drawTerrain();
        drawFood();
        drawCreatures();

        tick++;
        if (tick > 100)
        {
            _world.tick();
            tick = 0;
        }
        // colored.draw(batch);
        _batch.end();
    }

    private void drawTerrain()
    {
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getEntityType(r, c) == EntityType.WALL)
                {
                    float x = c * BLOCK_SIZE;
                    float y = getHeight() - r * BLOCK_SIZE - BLOCK_SIZE;
                    float originX = BLOCK_SIZE / 2;
                    float originY = BLOCK_SIZE / 2;
                    float width = BLOCK_SIZE;
                    float height = BLOCK_SIZE;
                    float scale = 1;
                    float rotation = 0; // In degrees
                    int srcX = BLOCK_SIZE;
                    int srcY = 0;
                    int srcW = BLOCK_SIZE;
                    int srcH = BLOCK_SIZE;
                    boolean flipX = false;
                    boolean flipY = false;
                    _batch.draw(_img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW,
                            srcH, flipX, flipY);
                }
            }
        }
    }

    private void drawFood()
    {
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getEntityType(r, c) == EntityType.FOOD)
                {
                    float x = c * BLOCK_SIZE;
                    float y = getHeight() - r * BLOCK_SIZE - BLOCK_SIZE;
                    float originX = BLOCK_SIZE / 2;
                    float originY = BLOCK_SIZE / 2;
                    float width = BLOCK_SIZE;
                    float height = BLOCK_SIZE;
                    float scale = 1;
                    float rotation = 0; // In degrees
                    int srcX = 2 * BLOCK_SIZE;
                    int srcY = 0;
                    int srcW = BLOCK_SIZE;
                    int srcH = BLOCK_SIZE;
                    boolean flipX = false;
                    boolean flipY = false;
                    _batch.draw(_img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW,
                            srcH, flipX, flipY);
                }
            }
        }
    }

    private void drawCreatures()
    {
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getEntityType(r, c) == EntityType.CREATURE)
                {
                    Creature creature = _world.getCreature(r, c);
                    float x = c * BLOCK_SIZE;
                    float y = getHeight() - r * BLOCK_SIZE - BLOCK_SIZE;
                    float originX = BLOCK_SIZE / 2;
                    float originY = BLOCK_SIZE / 2;
                    float width = BLOCK_SIZE;
                    float height = BLOCK_SIZE;
                    float scale = 1;
                    float rotation = (float) (Direction.getRadiansFromNorth(creature.getFacing()) * 180 / (Math.PI));
                    int srcX = 2 * BLOCK_SIZE;
                    int srcY = 2 * BLOCK_SIZE;
                    int srcW = BLOCK_SIZE;
                    int srcH = BLOCK_SIZE;
                    boolean flipX = false;
                    boolean flipY = false;
                    _batch.draw(_img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW,
                            srcH, flipX, flipY);
                }
            }
        }
    }

    public static int getHeight()
    {
        return SIZE * BLOCK_SIZE;
    }

    public static int getWidth()
    {
        return SIZE * BLOCK_SIZE;
    }
}
