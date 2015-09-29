package com.johnuckele.vivarium.web.client;

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
import com.johnuckele.vivarium.core.Blueprint;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.visualization.animation.SpriteRenderer;
import com.johnuckele.vivarium.visualization.animation.Visualizer;

public class VivariumWeb implements AnimationCallback, EntryPoint, LoadHandler
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    Canvas canvas;
    Context2d context;
    Image spriteImage;
    ImageElement spriteImageElement;
    World world;
    SpriteRenderer renderer;
    double lastTickTimestamp = 0;
    int tick = 0;
    GWTGraphics gwtGraphics;
    GWTScheduler gwtScheduler;
    Visualizer visualizer;

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
        canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(world.getWorldWidth() * VivariumWeb.PIXEL_BLOCK_SIZE);
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
        gwtGraphics.setResources(context, spriteImageElement);

        // Once we add this image, the browser will start loading and pick up
        // again in this.onLoad
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
