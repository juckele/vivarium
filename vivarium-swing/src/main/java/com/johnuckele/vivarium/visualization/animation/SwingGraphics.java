package com.johnuckele.vivarium.visualization.animation;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

import com.johnuckele.vivarium.core.Direction;

public class SwingGraphics extends GraphicalSystem
{
    private static ImageIcon IMAGE_ICON = new ImageIcon("src/main/resources/sprites.png");
    private static Image IMAGE = IMAGE_ICON.getImage();

    private Graphics2D _graphics;
    private ImageObserver _observer;

    public SwingGraphics()
    {

    }

    public void setResources(Graphics2D graphics, ImageObserver observer)
    {
        _graphics = graphics;
        _observer = observer;
    }

    @Override
    public void drawImage(int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Direction heading)
    {
        _graphics.rotate(-Direction.getRadiansFromNorth(heading), (dx1 + dx2) / 2.0, (dy1 + dy2) / 2.0);
        _graphics.drawImage(IMAGE, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, _observer);
        _graphics.rotate(Direction.getRadiansFromNorth(heading), (dx1 + dx2) / 2.0, (dy1 + dy2) / 2.0);

    }

}
