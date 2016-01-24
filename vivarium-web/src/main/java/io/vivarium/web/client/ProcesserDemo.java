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

import io.vivarium.core.processor.Processor;
import io.vivarium.core.processor.RandomGenerator;
import io.vivarium.visualization.ProcessorRenderer;

public class ProcesserDemo implements AnimationCallback, EntryPoint, LoadHandler
{
    public static final int PIXEL_BLOCK_SIZE = 32;

    private Canvas _canvas;
    private Processor _processer;
    private ProcessorRenderer _processerRenderer;
    private GWTGeometricGraphics gwtGraphics;

    @Override
    public void onModuleLoad()
    {
        // Build processer
        _processer = new RandomGenerator(6);
        _processerRenderer = new ProcessorRenderer(_processer);

        // Make canvas
        _canvas = Canvas.createIfSupported();
        _canvas.setCoordinateSpaceWidth(300);
        _canvas.setCoordinateSpaceHeight(300);
        RootPanel.get().add(_canvas);

        // Draw stuff?
        gwtGraphics = new GWTGeometricGraphics(_processerRenderer, this);

        Context2d context = _canvas.getContext2d();
        gwtGraphics.setResources(context);
        gwtGraphics.requestRender();
    }

    @Override
    public void execute(double timestamp)
    {
        _processerRenderer.onRender(gwtGraphics);
    }

    @Override
    public void onLoad(LoadEvent event)
    {
        // Nothing required here, this works in src only for now.
    }

}
