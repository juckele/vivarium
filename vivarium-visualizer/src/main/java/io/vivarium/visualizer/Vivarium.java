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
        batch.draw(img, 0, 0);

        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getEntityType(r, c) == EntityType.WALL)
                {
                    float x = c * 32;
                    float y = r * 32;
                    float originX = 16;
                    float originY = 16;
                    float width = 32;
                    float height = 32;
                    float scale = 1;
                    float rotation = (float) (360 * Math.random());
                    int srcX = 32;
                    int srcY = 0;
                    int srcW = 32;
                    int srcH = 32;
                    boolean flipX = false;
                    boolean flipY = false;
                    batch.draw(img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW,
                            srcH, flipX, flipY);
                }
            }
        }

        colored.draw(batch);
        batch.end();
    }
}
