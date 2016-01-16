/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import org.realityforge.gwt.websockets.client.WebSocket;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import io.vivarium.core.brain.Brain;
import io.vivarium.core.brain.RandomBrain;
import io.vivarium.net.messages.Message;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.visualization.animation.Visualizer;

public class BrainDemo implements AnimationCallback, EntryPoint, LoadHandler
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    private Canvas _canvas;
    private Canvas _tempCanvas;
    private Canvas _tempCanvas2;
    private Brain _brain;
    private VivariumObjectCollection _collection;
    // private GWTGraphics gwtGraphics;
    // private GWTScheduler gwtScheduler;
    private Visualizer visualizer;
    private Image baseImage;
    private Image colorImage;

    private boolean _debug = false;
    private boolean _tickEveryFrame = false;
    private int _ticksPerStep = 1;

    private Label _fpsCounter;

    private UUID _resourceID;
    private ObjectMapper<Message> _mapper;

    private WebSocket _webSocket;
    private boolean _saveOnClose;

    private static final String DEBUG_KEY = "debug";
    private static final String TICK_KEY = "tick";
    private static final String WEB_SOCKET_KEY = "ws";
    // save on close functionality also requires ws functionality to be turned on
    private static final String SAVE_ON_CLOSE = "save";

    @Override
    public void onModuleLoad()
    {
        buildBrain();
        setUpGraphics();
        displayWorld();
    }

    private void buildBrain()
    {
        _brain = new RandomBrain(6);
    }

    private void setUpGraphics()
    {
        // Set up the visualizer graphics
        // gwtGraphics = new GWTGraphics(this);
        // gwtScheduler = new GWTScheduler(this, _tickEveryFrame, _ticksPerStep);
        // visualizer = new Visualizer(world, gwtGraphics, gwtScheduler);
    }

    private void displayWorld()
    {
        if (_debug)
        {
            _fpsCounter = new Label();
            _fpsCounter.getElement().getStyle().setColor("white");
            RootPanel.get().add(_fpsCounter);
        }

        _tempCanvas = Canvas.createIfSupported();
        _tempCanvas.setCoordinateSpaceWidth(300);
        _tempCanvas.setCoordinateSpaceHeight(300);
        // RootPanel.get().add(_tempCanvas);
        _tempCanvas2 = Canvas.createIfSupported();
        _tempCanvas2.setCoordinateSpaceWidth(300);
        _tempCanvas2.setCoordinateSpaceHeight(300);
        // RootPanel.get().add(_tempCanvas2);

        // The canvas is our graphical space for all of the world display
        _canvas = Canvas.createIfSupported();
        // _canvas.setCoordinateSpaceWidth(world.getWorldWidth() * BrainDemo.PIXEL_BLOCK_SIZE);
        // _canvas.setCoordinateSpaceHeight(world.getWorldHeight() * BrainDemo.PIXEL_BLOCK_SIZE);
        RootPanel.get().add(_canvas);

        // All of the sprites are loaded from a single sprites image
        baseImage = new Image();
        baseImage.setUrl("raw_sprites.png");
        baseImage.addLoadHandler(this);
        baseImage.setVisible(false);

        // Once we add this image, the browser will start loading.
        // When we get an event that load is completed, we can start doing work again.
        RootPanel.get().add(baseImage);
    }

    private void allImagesLoaded()
    {
        // Give the canvases Context2d and the sprit ImageElement to the graphical delegate for future use.
        Context2d context = _canvas.getContext2d();
        ImageElement colorImageElement = ImageElement.as(colorImage.getElement());
        // gwtGraphics.setResources(context, colorImageElement);
        // Start the visualizer as soon as everything is loaded.
        visualizer.start();
    }

    @Override
    public void onLoad(LoadEvent event)
    {
        if (event.getSource() == baseImage)
        {
            Context2d context1 = _tempCanvas.getContext2d();
            Context2d context2 = _tempCanvas2.getContext2d();
            ImageElement baseImageElement = ImageElement.as(baseImage.getElement());
            context1.drawImage(baseImageElement, 0, 0);
            ImageData data;

            // Copy empty square
            data = context1.getImageData(0, 0, 32, 32);
            context2.putImageData(data, 0, 0);

            // Copy walls, make them dark gray
            data = context1.getImageData(32, 0, 32, 32);
            scaleData(data, 0.2f, 0.2f, 0.2f);
            context2.putImageData(data, 32, 0);

            // Copy food, make it green
            data = context1.getImageData(64, 0, 32, 32);
            scaleData(data, 0.0f, 0.5f, 0.0f);
            context2.putImageData(data, 64, 0);

            // Copy halos
            data = context1.getImageData(0, 32, 96, 32);
            context2.putImageData(data, 0, 32);

            // Copy creature, make a red one
            data = context1.getImageData(0, 64, 96, 32);
            scaleData(data, 0.8f, 0.0f, 0.0f);
            context2.putImageData(data, 0, 64);
            // Copy creature, make a teal one
            data = context1.getImageData(0, 64, 96, 32);
            scaleData(data, 0.0f, 0.8f, 0.8f);
            context2.putImageData(data, 0, 96);
            // Copy creature, make a purple one
            data = context1.getImageData(0, 64, 96, 32);
            scaleData(data, 0.4f, 0.0f, 0.8f);
            context2.putImageData(data, 0, 128);

            // Load the color image now
            colorImage = new Image();
            colorImage.setUrl(_tempCanvas2.toDataUrl());
            colorImage.addLoadHandler(this);
            colorImage.setVisible(false);
            RootPanel.get().add(colorImage);
        }
        if (event.getSource() == colorImage)
        {
            allImagesLoaded();
        }
    }

    private void scaleData(ImageData data, float redFactor, float greenFactor, float blueFactor)
    {
        for (int x = 0; x < data.getWidth(); x++)
        {
            for (int y = 0; y < data.getHeight(); y++)
            {
                data.setRedAt((int) (data.getRedAt(x, y) * redFactor), x, y);
                data.setGreenAt((int) (data.getGreenAt(x, y) * greenFactor), x, y);
                data.setBlueAt((int) (data.getBlueAt(x, y) * blueFactor), x, y);
            }
        }
    }

    @Override
    public void execute(double timestamp)
    {
        // Render a frame
        // gwtGraphics.renderFrame();

        // Schedule the next frame
        // gwtScheduler.execute(timestamp);
    }

    public void setCurrentFrameRate(double fps)
    {
        if (_debug)
        {
            _fpsCounter.setText(String.valueOf((int) fps));
        }
    }

}
