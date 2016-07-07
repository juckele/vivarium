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
    public void drawWideLine(int x1, int y1, int x2, int y2, int width)
    {
        System.out.println("Draw wide line " + x1 + ", " + y1 + " to " + x2 + "," + y2 + "!");
        int dx = x2 - x1;
        int dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        int normalX = (int) ((-dy * width) / (2 * length));
        int normalY = (int) ((dx * width) / (2 * length));
        int[] polyX = new int[4];
        int[] polyY = new int[4];
        polyX[0] = x1 + normalX;
        polyX[1] = x2 + normalX;
        polyX[2] = x2 - normalX;
        polyX[3] = x1 - normalX;
        polyY[0] = y1 + normalY;
        polyY[1] = y2 + normalY;
        polyY[2] = y2 - normalY;
        polyY[3] = y1 - normalY;

        _graphics.setColor(_fillColor);
        _graphics.fillPolygon(polyX, polyY, 4);
        _graphics.setColor(_penColor);
        _graphics.drawPolygon(polyX, polyY, 4);
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
