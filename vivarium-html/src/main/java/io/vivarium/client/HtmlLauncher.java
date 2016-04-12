package io.vivarium.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import io.vivarium.visualizer.Vivarium;

public class HtmlLauncher extends GwtApplication
{

    @Override
    public GwtApplicationConfiguration getConfig()
    {
        return new GwtApplicationConfiguration((int) (Vivarium.getWidth() * 1.5), Vivarium.getHeight());
    }

    @Override
    public ApplicationListener createApplicationListener()
    {
        return new Vivarium();
    }
}