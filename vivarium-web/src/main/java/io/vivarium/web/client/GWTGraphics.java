/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.web.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import io.vivarium.core.Direction;
import io.vivarium.visualization.animation.WorldGraphics;

public class GWTGraphics extends WorldGraphics
{
    private Context2d _context;
    private ImageElement _imageElement;
    private VivariumWeb _webApp;
    private long _lastFrameTime = System.currentTimeMillis();

    public GWTGraphics(VivariumWeb webApp)
    {
        _webApp = webApp;
    }

    public void setResources(Context2d context, ImageElement imageElement)
    {
        _context = context;
        _imageElement = imageElement;
    }

    @Override
    public void drawImage(int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, int colorOffset,
            Direction heading)
    {
        int blockSize = dx2 - dx1;
        int colorOffsetPixels = blockSize * colorOffset;

        _context.translate(dx1 + blockSize / 2.0, dy1 + blockSize / 2.0);
        _context.rotate(-Direction.getRadiansFromNorth(heading));

        _context.drawImage(_imageElement, sx1, sy1 + colorOffsetPixels, sx2 - sx1, sy2 - sy1, -blockSize / 2.0,
                -blockSize / 2.0, blockSize, blockSize);

        _context.rotate(Direction.getRadiansFromNorth(heading));
        _context.translate(-dx1 - blockSize / 2.0, -dy1 - blockSize / 2.0);
    }

    @Override
    public void requestRender()
    {
        AnimationScheduler.get().requestAnimationFrame(_webApp);
    }

    @Override
    protected void startFrameRender()
    {
        long currentFrameTime = System.currentTimeMillis();
        if (currentFrameTime == _lastFrameTime)
        {
            _webApp.setCurrentFrameRate(Double.POSITIVE_INFINITY);
        }
        else
        {
            _webApp.setCurrentFrameRate(1000.0 / (currentFrameTime - _lastFrameTime));
            _lastFrameTime = currentFrameTime;
        }
        // Do nothing
    }

    @Override
    protected void endFrameRender()
    {
        // Do nothing
    }

}
