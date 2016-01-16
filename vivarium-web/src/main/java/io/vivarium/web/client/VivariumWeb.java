/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListenerAdapter;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.visualization.animation.Visualizer;

public class VivariumWeb implements AnimationCallback, EntryPoint, LoadHandler
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    private Canvas _canvas;
    private Canvas _tempCanvas;
    private Canvas _tempCanvas2;
    private World world;
    private VivariumObjectCollection _collection;
    private GWTWorldGraphics gwtGraphics;
    private GWTScheduler gwtScheduler;
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
        // Get options from parameters
        String debug = Window.Location.getParameter(DEBUG_KEY);
        if (debug != null && debug.toLowerCase().equals("true"))
        {
            _debug = true;
        }
        String tick = Window.Location.getParameter(TICK_KEY);
        if (tick != null)
        {
            if (tick.toLowerCase().equals("perframe"))
            {
                _tickEveryFrame = true;
                _ticksPerStep = 1;
            }
            else
            {
                try
                {
                    _ticksPerStep = Integer.parseInt(tick);
                    _tickEveryFrame = true;
                }
                catch (NumberFormatException e)
                {
                    // Do nothing
                }
            }
        }
        String ws = Window.Location.getParameter(WEB_SOCKET_KEY);
        if (ws != null)
        {
            if (ws.toLowerCase().equals("true"))
            {
                _resourceID = UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d");
            }
            else
            {
                _resourceID = UUID.fromString(ws);
            }
            String saveOnClose = Window.Location.getParameter(SAVE_ON_CLOSE);
            if (saveOnClose != null)
            {
                _saveOnClose = true;
                Window.alert("I want to save on close!");
            }
            startWS();
        }
        else
        {
            buildWorld();
            setUpGraphics();
            displayWorld();
        }
    }

    private void buildWorld()
    {
        // Build the world
        Blueprint blueprint = Blueprint.makeDefault();
        Species s = Species.makeDefault();
        ArrayList<Species> species = new ArrayList<Species>();
        species.add(s);
        blueprint.setSpecies(species);
        blueprint.setSize(30);
        blueprint.setWidth(45);
        world = new World(blueprint);
    }

    private void setUpGraphics()
    {
        // Set up the visualizer graphics
        gwtGraphics = new GWTWorldGraphics(this);
        gwtScheduler = new GWTScheduler(this, _tickEveryFrame, _ticksPerStep);
        visualizer = new Visualizer(world, gwtGraphics, gwtScheduler);
    }

    private void startWS()
    {
        _mapper = GWT.create(MessageMapper.class);
        // Pledge p = new Pledge(UUID.randomUUID());
        // String encoding = _mapper.write(p);
        // Window.alert(encoding);

        _webSocket = WebSocket.newWebSocketIfSupported();
        if (_webSocket != null)
        {
            _webSocket.setListener(new WebSocketListenerAdapter()
            {
                @Override
                public void onOpen(@Nonnull final WebSocket webSocket)
                {
                    // After we have connected we can send
                    RequestResourceMessage request = new RequestResourceMessage(_resourceID, ResourceFormat.GWT_STREAM);
                    webSocket.send(_mapper.write(request));
                }

                @Override
                public void onMessage(@Nonnull final WebSocket webSocket, @Nonnull final String data)
                {
                    Message incomingMessage = _mapper.read(data);
                    if (incomingMessage instanceof SendResourceMessage)
                    {
                        SendResourceMessage sendResource = (SendResourceMessage) incomingMessage;
                        _collection = (VivariumObjectCollection) Streamer.get()
                                .fromString(sendResource.getDataString());

                        // Finish loading
                        world = _collection.getFirst(World.class);
                        setUpGraphics();
                        displayWorld();
                    }
                    // After we receive a message back we can close the socket
                    // webSocket.close();
                }
            });
            _webSocket.connect("ws://localhost:13731/");
            if (_saveOnClose)
            {
                Window.addCloseHandler(new CloseHandler<Window>()
                {
                    @Override
                    public void onClose(CloseEvent<Window> event)
                    {
                        Window.alert("Can I please save?");
                        if (_webSocket != null && _webSocket.isConnected())
                        {
                            // After we have connected we can send
                            SendResourceMessage sendMessage = new SendResourceMessage(_resourceID,
                                    Streamer.get().toString(_collection), ResourceFormat.GWT_STREAM);
                            _webSocket.send(_mapper.write(sendMessage));
                            _webSocket.close();
                        }
                    }
                });
            }
        }
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
        _canvas.setCoordinateSpaceWidth(world.getWorldWidth() * VivariumWeb.PIXEL_BLOCK_SIZE);
        _canvas.setCoordinateSpaceHeight(world.getWorldHeight() * VivariumWeb.PIXEL_BLOCK_SIZE);
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
        gwtGraphics.setResources(context, colorImageElement);
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
        gwtGraphics.renderFrame();

        // Schedule the next frame
        gwtScheduler.execute(timestamp);
    }

    public void setCurrentFrameRate(double fps)
    {
        if (_debug)
        {
            _fpsCounter.setText(String.valueOf((int) fps));
        }
    }

}
