package io.vivarium.web.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import io.vivarium.visualizer.Vivarium;

public class HtmlLauncher extends GwtApplication
{

    @Override
    public GwtApplicationConfiguration getConfig()
    {
        return new GwtApplicationConfiguration(1024, 786);
    }

    @Override
    public ApplicationListener createApplicationListener()
    {
        return new Vivarium();
    }
}