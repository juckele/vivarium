package io.vivarium.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import io.vivarium.visualizer.EmojiVivarium;
import io.vivarium.visualizer.Vivarium;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int) (Vivarium.getWidth() * 1.5);
        config.height = Vivarium.getHeight();
        config.resizable = false;
        new LwjglApplication(new EmojiVivarium(), config);
    }
}
