/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.RootPanel;

import io.vivarium.core.brain.Brain;
import io.vivarium.core.brain.RandomBrain;
import io.vivarium.visualization.GeometricGraphics;
import io.vivarium.visualization.GraphicalController;

public class BrainDemo implements AnimationCallback, EntryPoint, LoadHandler, GraphicalController
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    private Canvas _canvas;
    private Brain _brain;
    private GWTGeometricGraphics gwtGraphics;

    @Override
    public void onModuleLoad()
    {
        // Build brain
        _brain = new RandomBrain(6);

        // Make canvas
        _canvas = Canvas.createIfSupported();
        _canvas.setCoordinateSpaceWidth(300);
        _canvas.setCoordinateSpaceHeight(300);
        RootPanel.get().add(_canvas);

        // Draw stuff?
        gwtGraphics = new GWTGeometricGraphics(this, this);

        Context2d context = _canvas.getContext2d();
        gwtGraphics.setResources(context);
        gwtGraphics.requestRender();
    }

    @Override
    public void execute(double timestamp)
    {
        // Render a frame
        // gwtGraphics.renderFrame();

        // Schedule the next frame
        // gwtScheduler.execute(timestamp);
    }

    @Override
    public void onRender(GeometricGraphics graphics)
    {
        graphics.drawRectangle(100, 100, 100, 100);
    }

    @Override
    public void onLoad(LoadEvent event)
    {
        // Nothing required here, this works in src only for now.
    }

}
