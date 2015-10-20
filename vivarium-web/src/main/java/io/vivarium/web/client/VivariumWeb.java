/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import java.util.ArrayList;

import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.visualization.animation.Visualizer;

public class VivariumWeb implements AnimationCallback, EntryPoint, LoadHandler
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    private Canvas _canvas;
    private World world;
    private GWTGraphics gwtGraphics;
    private GWTScheduler gwtScheduler;
    private Visualizer visualizer;

    @Override
    public void onModuleLoad()
    {
        Blueprint blueprint = Blueprint.makeDefault();
        Species s = Species.makeDefault();
        ArrayList<Species> species = new ArrayList<Species>();
        species.add(s);
        blueprint.setSpecies(species);
        blueprint.setSize(30);
        blueprint.setWidth(45);
        world = new World(blueprint);
        gwtGraphics = new GWTGraphics(this);
        gwtScheduler = new GWTScheduler(this);
        visualizer = new Visualizer(world, gwtGraphics, gwtScheduler);
        displayWorld();
    }

    private void displayWorld()
    {
        // The canvas is our graphical space for all of the world display
        _canvas = Canvas.createIfSupported();
        _canvas.setCoordinateSpaceWidth(world.getWorldWidth() * VivariumWeb.PIXEL_BLOCK_SIZE);
        _canvas.setCoordinateSpaceHeight(world.getWorldHeight() * VivariumWeb.PIXEL_BLOCK_SIZE);
        RootPanel.get().add(_canvas);
        Context2d context = _canvas.getContext2d();

        // All of the sprites are loaded from a single sprites image
        Image spriteImage = new Image();
        spriteImage.setUrl("sprites.png");
        ImageElement spriteImageElement = ImageElement.as(spriteImage.getElement());
        spriteImage.addLoadHandler(this);
        spriteImage.setVisible(false);

        // Give the canvases Context2d and the sprit ImageElement to the graphical delegate for future use.
        gwtGraphics.setResources(context, spriteImageElement);

        // Once we add this image, the browser will start loading.
        // When we get an event that load is completed, we can start doing work again.
        RootPanel.get().add(spriteImage);
    }

    private void allImagesLoaded()
    {
        // Start the visualizer as soon as everything is loaded.
        visualizer.start();
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
        // Render a frame
        gwtGraphics.renderFrame();

        // Schedule the next frame
        gwtScheduler.execute(timestamp);
    }

}
