package io.vivarium.visualizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Vivarium extends ApplicationAdapter
{
    SpriteBatch batch;
    Texture img;
    Sprite colored;

    @Override
    public void create()
    {
        batch = new SpriteBatch();
        img = new Texture("sprites.png");
        colored = new Sprite(img);
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        colored.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1f);
        colored.setPosition(100, 00);
        batch.begin();
        batch.draw(img, 0, 0);
        colored.draw(batch);
        batch.end();
    }
}
