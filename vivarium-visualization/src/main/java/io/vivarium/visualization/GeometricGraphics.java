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

    public abstract void drawWideLine(int x1, int y1, int x2, int y2, int width);

    public abstract void drawCircle(int x, int y, int w, int h);

    public abstract void drawRectangle(int x, int y, int w, int h);

    public abstract void setFillColor(float r, float g, float b);

    public abstract void requestRender();

}
