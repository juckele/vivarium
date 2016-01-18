package io.vivarium.visualization;

import io.vivarium.core.brain.Brain;

public class BrainRenderer implements GraphicalController
{
    private Brain _brain;

    public BrainRenderer(Brain brain)
    {
        this._brain = brain;
    }

    @Override
    public void onRender(GeometricGraphics graphics)
    {
        graphics.drawRectangle(0, 0, 20, _brain.getOutputCount() * 20);
    }

}
