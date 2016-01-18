package io.vivarium.web.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.dom.client.Context2d;

import io.vivarium.visualization.GeometricGraphics;
import io.vivarium.visualization.GraphicalController;

public class GWTGeometricGraphics extends GeometricGraphics
{
    private AnimationCallback _animationCallback;
    private Context2d _context;

    public GWTGeometricGraphics(GraphicalController controller, AnimationCallback animationCallback)
    {
        super(controller);
        _animationCallback = animationCallback;
    }

    public void setResources(Context2d context)
    {
        _context = context;
    }

    @Override
    public void drawRectangle(int x, int y, int w, int h)
    {
        _context.rect(x, y, w, h);
        _context.stroke();
    }

    @Override
    public void requestRender()
    {
        AnimationScheduler.get().requestAnimationFrame(_animationCallback);
    }
}
