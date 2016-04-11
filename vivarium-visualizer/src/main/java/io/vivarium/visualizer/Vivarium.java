package io.vivarium.visualizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

    // Low Level Graphics information
    SpriteBatch batch;
    Texture img;
    Sprite colored;

    @Override
    public void create()
    {
        _blueprint = WorldBlueprint.makeDefault();
        _blueprint.setSize(SIZE);
        _world = new World(_blueprint);

        batch = new SpriteBatch();
        img = new Texture("sprites.png");
        colored = new Sprite(img);
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        colored.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1f);
        colored.setPosition(100, 00);
        batch.begin();
        // batch.draw(img, 0, 0);

        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getEntityType(r, c) == EntityType.WALL)
                {
                    float x = c * BLOCK_SIZE;
                    float y = r * BLOCK_SIZE;
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
                    batch.draw(img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW,
                            srcH, flipX, flipY);
                }
            }
        }

        // colored.draw(batch);
        batch.end();
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
