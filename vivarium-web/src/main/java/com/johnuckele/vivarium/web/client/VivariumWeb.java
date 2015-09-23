package com.johnuckele.vivarium.web.client;

import java.util.ArrayList;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtstreamer.client.Streamer;
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Species;

public class VivariumWeb implements AnimationCallback, EntryPoint, LoadHandler
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    Canvas canvas;
    Context2d context;
    Image spriteImage;
    ImageElement spriteImageElement;
    WebWorld world;
    SpriteRenderer renderer;
    double lastTickTimestamp = 0;
    int tick = 0;

    @Override
    public void onModuleLoad()
    {
        Blueprint blueprint = Blueprint.makeDefault();
        Species s = Species.makeDefault();
        ArrayList<Species> species = new ArrayList<Species>();
        species.add(s);
        blueprint.setSpecies(species);
        blueprint.setSize(40);
        world = new WebWorld(blueprint);
        displayWorld();
    }

    private void displayWorld()
    {
        // The canvas is our graphical space for all of the world display
        canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(world.getWorldHeight() * VivariumWeb.PIXEL_BLOCK_SIZE);
        canvas.setCoordinateSpaceHeight(world.getWorldHeight() * VivariumWeb.PIXEL_BLOCK_SIZE);
        RootPanel.get().add(canvas);
        context = canvas.getContext2d();

        // All of the sprites are loaded from a single sprites image
        spriteImage = new Image();
        spriteImage.setUrl("sprites.png");
        spriteImageElement = ImageElement.as(spriteImage.getElement());
        spriteImage.addLoadHandler(this);
        spriteImage.setVisible(false);

        // The renderer is used as a mid level tool to draw sprites
        // on the canvas
        renderer = new SpriteRenderer(context, spriteImageElement);

        // Once we add this image, the browser will start loading and pick up
        // again in this.onLoad
        RootPanel.get().add(spriteImage);
    }

    private void allImagesLoaded()
    {
        // Do a base render, placing all of the fixed walls and filling the
        // florrs
        world.terrainRender(renderer);
        // Once we've done the base render, we'll kick off the ongoing
        // animations
        AnimationScheduler.get().requestAnimationFrame(this);
    }

    @Override
    public void onLoad(LoadEvent event)
    {
        // For now we only have one image so if we get any loaded
        // events we're immediately good to go.
        allImagesLoaded();
    }

    @Override
    public void execute(double timestamp)
    {
        if (tick > 0)
        {
            if (timestamp >= lastTickTimestamp + 1000)
            {
                lastTickTimestamp = timestamp;
                world.tick();
            }
        }
        else
        {
            if (lastTickTimestamp == 0)
            {
                lastTickTimestamp = timestamp;
            }
            if (timestamp >= lastTickTimestamp + 1000)
            {
                lastTickTimestamp = timestamp;
                world.tick();
            }
        }
        WebWorld worldCopy = Streamer.get().deepCopy(world);

        worldCopy.terrainRender(renderer);
        int milliseconds = (int) (timestamp % 1000);
        worldCopy.actorRender(renderer, milliseconds);

        // No reason to ever stop animating...
        AnimationScheduler.get().requestAnimationFrame(this);
    }

}
