/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class SwingGeometricGraphics extends GeometricGraphics
{
    private Graphics2D _graphics;
    private JPanel _observer;
    private Color _fillColor;
    private Color _penColor;

    public SwingGeometricGraphics(GraphicalController controller, JPanel observer)
    {
        super(controller);
        _observer = observer;
        _fillColor = Color.WHITE;
        _penColor = Color.BLACK;
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
        _graphics.setColor(_fillColor);
        _graphics.fillRect(x, y, w, h);
        _graphics.setColor(_penColor);
        _graphics.drawRect(x, y, w, h);
    }

    @Override
    public void drawCircle(int x, int y, int w, int h)
    {
        _graphics.setColor(_fillColor);
        _graphics.fillOval(x, y, w, h);
        _graphics.setColor(_penColor);
        _graphics.drawOval(x, y, w, h);
    }

    @Override
    public void setFillColor(float r, float g, float b)
    {
        _fillColor = new Color(r, g, b);
    }
}
