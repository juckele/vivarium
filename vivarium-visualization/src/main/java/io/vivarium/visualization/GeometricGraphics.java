/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization;

public abstract class GeometricGraphics
{
    private GraphicalController _controller;

    public GeometricGraphics(GraphicalController controller)
    {
        super();
        _controller = controller;
    }

    public void executeRender()
    {
        _controller.onRender(this);
    }

    public abstract void drawRectangle(int x, int y, int w, int h);

    public abstract void requestRender();
}
