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
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
                    batch.draw(img, r * 32, c * 32, 32, 0, 32, 32);
                    // batch.draw(img, 200, 200, 0, 0, 16, 16, 1, 1, 0, 8, 8, 16, 16, false, false);
                }
            }
        }

        // colored.draw(batch);
        batch.end();
    }
}
