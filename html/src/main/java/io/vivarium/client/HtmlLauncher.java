package io.vivarium.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import io.vivarium.html.Vivarium;

public class HtmlLauncher extends GwtApplication
{

    @Override
    public GwtApplicationConfiguration getConfig()
    {
        return new GwtApplicationConfiguration(480, 320);
    }

    @Override
    public ApplicationListener createApplicationListener()
    {
        return new Vivarium();
    }
}
