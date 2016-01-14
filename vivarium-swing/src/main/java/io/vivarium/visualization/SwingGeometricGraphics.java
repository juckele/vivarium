/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization;

import java.awt.Graphics2D;

import javax.swing.JPanel;

public class SwingGeometricGraphics extends GeometricGraphics
{
    private Graphics2D _graphics;
    private JPanel _observer;

    public SwingGeometricGraphics(GraphicalController controller, JPanel observer)
    {
        super(controller);
        _observer = observer;
    }

    public void setResources(Graphics2D graphics)
    {
        _graphics = graphics;
    }

    @Override
    public void requestRender()
    {
        _observer.repaint();
    }

    @Override
    public void drawRectangle(int x, int y, int w, int h)
    {
        _graphics.drawRect(x, y, w, h);
    }
}
