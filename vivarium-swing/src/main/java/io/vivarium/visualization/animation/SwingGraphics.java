/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.visualization.animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import io.vivarium.core.Direction;

public class SwingGraphics extends GraphicalDelegate
{
    private static ImageIcon IMAGE_ICON = new ImageIcon("src/main/resources/sprites.png");
    private static Image IMAGE = IMAGE_ICON.getImage();

    private Graphics2D _graphics;
    private JPanel _observer;

    public SwingGraphics(JPanel observer)
    {
        _observer = observer;
    }

    public void setResources(Graphics2D graphics)
    {
        _graphics = graphics;
    }

    @Override
    protected void startFrameRender()
    {
        _graphics.setColor(Color.BLACK);
        _graphics.fillRect(0, 0, _observer.getWidth(), _observer.getHeight());
    }

    @Override
    protected void endFrameRender()
    {
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void drawImage(int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, int colorOffset,
            Direction heading)
    {
        int blockSize = dx2 - dx1;
        int colorOffsetPixels = blockSize * colorOffset;
        _graphics.rotate(-Direction.getRadiansFromNorth(heading), (dx1 + dx2) / 2.0, (dy1 + dy2) / 2.0);
        _graphics.drawImage(IMAGE, dx1, dy1, dx2, dy2, sx1, sy1 + colorOffsetPixels, sx2, sy2 + colorOffsetPixels,
                _observer);
        _graphics.rotate(Direction.getRadiansFromNorth(heading), (dx1 + dx2) / 2.0, (dy1 + dy2) / 2.0);

    }

    @Override
    public void requestRender()
    {
        _observer.repaint();
    }
}
