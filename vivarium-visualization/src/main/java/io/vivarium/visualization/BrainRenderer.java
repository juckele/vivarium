package io.vivarium.visualization;

import io.vivarium.core.brain.Brain;
import io.vivarium.core.brain.NeuralNetworkBrain;

public class BrainRenderer implements GraphicalController
{
    public static final int NODE_SIZE = 60;
    public static final int NODE_SPREAD = 200;
    public static final int LAYER_SPREAD = 200;
    public static final int MAX_LINE_WIDTH = 20;
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
        double[] inputs = new double[inputCount];
        double[] outputs = _brain.outputs(inputs);

        // Layer
        // graphics.setFillColor(0.5f, 0.5f, 0.5f);
        // graphics.drawRectangle(NODE_SIZE, NODE_SIZE, LAYER_SPREAD, Math.max(inputCount, outputCount) * NODE_SPREAD);

        // Connections
        NeuralNetworkBrain nnBrain = (NeuralNetworkBrain) _brain;
        double[][][] weights = nnBrain.getWeights();
        for (int inputIndex = 0; inputIndex < inputCount; inputIndex++)
        {
            for (int outputIndex = 0; outputIndex < outputCount; outputIndex++)
            {
                float weight = (float) ((weights[0][outputIndex][2 + inputIndex] + 1) / 2);
                graphics.setFillColor(weight, weight, weight);
                graphics.drawWideLine(0 + NODE_SIZE / 2, NODE_SIZE + inputIndex * NODE_SPREAD + NODE_SIZE / 2,
                        LAYER_SPREAD + NODE_SIZE + NODE_SIZE / 2, NODE_SIZE + outputIndex * NODE_SPREAD + NODE_SIZE / 2,
                        MAX_LINE_WIDTH);
            }
        }

        // Inputs
        for (int i = 0; i < inputCount; i++)
        {
            float activation = (float) inputs[i];
            activation = (activation + 1) / 2;
            graphics.setFillColor(activation, activation, activation);
            graphics.drawCircle(0, NODE_SIZE + i * NODE_SPREAD, NODE_SIZE, NODE_SIZE);
        }

        // Outputs
        for (int i = 0; i < outputCount; i++)
        {
            float activation = (float) outputs[i];
            activation = (activation + 1) / 2;
            graphics.setFillColor(activation, activation, activation);
            graphics.drawCircle(LAYER_SPREAD + NODE_SIZE, NODE_SIZE + i * NODE_SPREAD, NODE_SIZE, NODE_SIZE);
        }
    }

}
