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
        int inputCount = _brain.getInputCount();
        int outputCount = _brain.getOutputCount();

        // Inputs
        double[] inputs = new double[inputCount];
        for (int i = 0; i < inputCount; i++)
        {
            float activation = (float) inputs[i];
            activation = (activation + 1) / 2;
            graphics.setFillColor(activation, activation, activation);
            graphics.drawCircle(0, 20 + i * 40, 20, 20);
        }

        // Layer
        graphics.setFillColor(0.5f, 0.5f, 0.5f);
        graphics.drawRectangle(20, 20, 60, Math.max(inputCount, outputCount) * 40);

        // Outputs
        double[] outputs = _brain.outputs(inputs);
        for (int i = 0; i < outputCount; i++)
        {
            float activation = (float) outputs[i];
            activation = (activation + 1) / 2;
            graphics.setFillColor(activation, activation, activation);
            graphics.drawCircle(80, 20 + i * 40, 20, 20);
        }
    }

}
