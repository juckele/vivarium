package io.vivarium.core.processor;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Functions;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class NeuralNetwork extends Processor
{
    // Weights represents all the weights in the neural network
    // weight[i][j][k] corresponds to the weight of the connection
    // for the jth node in the layer coming from the kth node in
    // the previous layer.
    // Each node has a two special previous values, a constant
    // bias unit with a value of 1 and a stochastic bias unit
    // with a normally distributed value between -1 and 1.
    @SerializedParameter
    private double[][] _weights;
    @SerializedParameter
    private double[] _outputs;

    private static int BIAS_UNIT_COUNT = 2;

    private NeuralNetwork()
    {
    }

    public NeuralNetwork(int inputCount, int outputCount, boolean randomInitialization, double normalizedLength)
    {
        super();
        Preconditions.checkArgument(inputCount > 0);
        Preconditions.checkArgument(outputCount > 0);
        constructWithDimensions(inputCount, outputCount, randomInitialization, normalizedLength);
    }

    private void constructWithDimensions(int inputCount, int outputCount, boolean randomInitialization,
            double normalizedLength)
    {
        this._outputs = new double[outputCount];
        this._weights = new double[outputCount][inputCount + BIAS_UNIT_COUNT];
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                _weights[i][j] = randomInitialization ? Rand.getInstance().getRandomDouble() : 1;
            }
        }
        if (normalizedLength != 0)
        {
            normalizeWeights(normalizedLength);
        }
    }

    public NeuralNetwork(NeuralNetworkBlueprint processorBlueprint, NeuralNetwork processor1, NeuralNetwork processor2)
    {
        // Construct the weight layer and store variables with the int based
        // constructor
        this(processor1.getInputCount(), processor1.getOutputCount(), false,
                processorBlueprint.getNormalizeAfterMutation());

        // Set all the weights with
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                // Mix first
                double randomValue = Rand.getInstance().getRandomPositiveDouble();
                // Sometimes mix the two values with a gaussian
                // approximation.
                if (randomValue < processorBlueprint.getInheritanceGaussianMixRate())
                {
                    // Radnom.nextGaussian generates a Gaussian with μ = 0
                    // and σ = 1
                    // but we want μ = 0.5 and σ = 0.5 to mix between
                    // numbers
                    // This can cause a mix to introduce values higher or
                    // lower than
                    // either parent, which is by design.
                    double gaussianRandomValue = Rand.getInstance().getRandomGaussian() / 2 + 0.5;
                    double weightDifference = processor2._weights[i][j] - processor1._weights[i][j];
                    _weights[i][j] = processor1._weights[i][j] + gaussianRandomValue * weightDifference;
                }
                // Otherwise pick one value
                else
                {
                    randomValue = Rand.getInstance().getRandomPositiveDouble();
                    if (randomValue < 0.5)
                    {
                        _weights[i][j] = processor1._weights[i][j];
                    }

                    else
                    {
                        _weights[i][j] = processor2._weights[i][j];
                    }
                }

                // Sometimes mutate
                randomValue = Rand.getInstance().getRandomPositiveDouble();
                if (randomValue < processorBlueprint.getMutationRate())
                {
                    randomValue = Rand.getInstance().getRandomPositiveDouble();
                    if (randomValue < processorBlueprint.getMutationSmallScaleRate())
                    {
                        // Gaussian multiplication mutation,
                        // μ = 1 and σ = 0.2
                        double gaussianRandomValue = Rand.getInstance().getRandomGaussian() / 5 + 1;
                        _weights[i][j] = gaussianRandomValue * _weights[i][j];
                    }
                    else
                    {
                        randomValue -= processorBlueprint.getMutationSmallScaleRate();
                        if (randomValue < processorBlueprint.getMutationRandomRate())
                        {
                            // Random mutation
                            _weights[i][j] = Rand.getInstance().getRandomDouble();
                        }
                        else
                        {
                            randomValue -= processorBlueprint.getMutationRandomRate();
                            if (randomValue < processorBlueprint.getMutationFlipRate())
                            {
                                // Flip mutation
                                _weights[i][j] = -_weights[i][j];
                            }
                            else
                            {
                                randomValue -= processorBlueprint.getMutationFlipRate();
                            }
                        }
                    }
                }
            }
        }
        if (processorBlueprint.getNormalizeAfterMutation() != 0)
        {
            normalizeWeights(processorBlueprint.getNormalizeAfterMutation());
        }
    }

    public void normalizeWeights(double normalizedLength)
    {
        double vectorLength = getGenomeLength();
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                _weights[i][j] = normalizedLength * _weights[i][j] / vectorLength;
            }
        }
    }

    public double getGenomeLength()
    {
        double sumOfSquares = 0;
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                sumOfSquares += Math.pow(_weights[i][j], 2);
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    public double[][] getWeights()
    {
        return (this._weights);
    }

    @Override
    public int getInputCount()
    {
        return _weights[0].length - BIAS_UNIT_COUNT;
    }

    @Override
    public int getOutputCount()
    {
        return _weights.length;
    }

    @Override
    public double[] outputs(double[] inputs)
    {
        // Clear the output units
        for (int i = 0; i < _outputs.length; i++)
        {
            _outputs[i] = 0;
        }

        computeLayerInPlace(inputs, _outputs, _weights);

        // Return
        return (_outputs);
    }

    @Override
    public double[] outputs()
    {
        return _outputs;
    }

    @SerializedParameter
    private int _hiddenLayerCount = 0;

    public static void computeLayerInPlace(double[] inputs, double[] outputs, double[][] weights)
    {
        for (int i = 0; i < outputs.length; i++)
        {
            // Bias units
            outputs[i] += weights[i][0] * 1;
            outputs[i] += weights[i][1] * Rand.getInstance().getRandomDouble();
            // prior units
            for (int j = 0; j < inputs.length; j++)
            {
                outputs[i] += weights[i][j + BIAS_UNIT_COUNT] * inputs[j];
            }
            // Scale for sigmoid
            outputs[i] = Functions.sigmoid(outputs[i]);
        }
    }

    public static void main(String[] args)
    {
        NeuralNetwork processor = new NeuralNetwork(3, 10, false, 0);
        System.out.println("Creating Processor...");
        System.out.println(processor);
        System.out.println("Processor Outputs for inputs");
        double[] inputs = { 0.0, 0.0 };
        System.out.println("" + Arrays.toString(inputs) + " -> " + Arrays.toString(processor.outputs(inputs)));
        System.out.println("Maximum output " + Arrays.toString(processor.outputs(inputs)));
        double[] inputs2 = { -1.0, 0.0 };
        System.out.println("" + Arrays.toString(inputs2) + " -> " + Arrays.toString(processor.outputs(inputs2)));
        System.out.println("Maximum output " + Arrays.toString(processor.outputs(inputs2)));
        double[] inputs3 = { 1.0, 0.0 };
        System.out.println("" + Arrays.toString(inputs3) + " -> " + Arrays.toString(processor.outputs(inputs3)));
        System.out.println("Maximum output " + Arrays.toString(processor.outputs(inputs3)));
        double[] inputs4 = { 0.5, 0.5 };
        System.out.println("" + Arrays.toString(inputs4) + " -> " + Arrays.toString(processor.outputs(inputs4)));
        System.out.println("Maximum output " + Arrays.toString(processor.outputs(inputs4)));

    }

    public static NeuralNetwork minProcessor(List<NeuralNetwork> processors)
    {
        NeuralNetwork minProcessor = new NeuralNetwork(processors.get(0).getInputCount(),
                processors.get(0).getOutputCount(), false, 0);
        // Set all the weights with
        for (NeuralNetwork processor : processors)
        {
            for (int i = 0; i < minProcessor._weights.length; i++)
            {
                for (int j = 0; j < minProcessor._weights[i].length; j++)
                {
                    minProcessor._weights[i][j] = Math.min(processor._weights[i][j], minProcessor._weights[i][j]);
                }
            }
        }
        return minProcessor;

    }

    public static NeuralNetwork maxProcessor(List<NeuralNetwork> processors)
    {
        NeuralNetwork maxProcessor = new NeuralNetwork(processors.get(0).getInputCount(),
                processors.get(0).getOutputCount(), false, 0);
        // Set all the weights with
        for (NeuralNetwork processor : processors)
        {
            for (int i = 0; i < maxProcessor._weights.length; i++)
            {
                for (int j = 0; j < maxProcessor._weights[i].length; j++)
                {
                    maxProcessor._weights[i][j] = Math.max(processor._weights[i][j], maxProcessor._weights[i][j]);
                }
            }
        }
        return maxProcessor;
    }

    public static NeuralNetwork medianProcessor(List<NeuralNetwork> processors)
    {
        NeuralNetwork medianProcessor = new NeuralNetwork(processors.get(0).getInputCount(),
                processors.get(0).getOutputCount(), false, 0);
        // Set all the weights with
        for (int i = 0; i < medianProcessor._weights.length; i++)
        {
            for (int j = 0; j < medianProcessor._weights[i].length; j++)
            {
                medianProcessor._weights[i][j] = 0;
            }
        }
        int processorsAveraged = processors.size();
        for (NeuralNetwork processor : processors)
        {
            for (int i = 0; i < medianProcessor._weights.length; i++)
            {
                for (int j = 0; j < medianProcessor._weights[i].length; j++)
                {
                    medianProcessor._weights[i][j] += processor._weights[i][j];
                }
            }
        }
        for (int i = 0; i < medianProcessor._weights.length; i++)
        {
            for (int j = 0; j < medianProcessor._weights[i].length; j++)
            {
                medianProcessor._weights[i][j] /= processorsAveraged;
            }
        }
        return medianProcessor;
    }

    public static NeuralNetwork standardDeviationProcessor(List<NeuralNetwork> processors,
            NeuralNetwork medianProcessor)
    {
        NeuralNetwork standardDeviationProcessor = new NeuralNetwork(medianProcessor.getInputCount(),
                medianProcessor.getOutputCount(), false, 0);
        for (int i = 0; i < standardDeviationProcessor._weights.length; i++)
        {
            for (int j = 0; j < standardDeviationProcessor._weights[i].length; j++)
            {
                standardDeviationProcessor._weights[i][j] = 0;
            }
        }
        int processorsAveraged = processors.size();
        double error;
        for (NeuralNetwork processor : processors)
        {
            for (int i = 0; i < standardDeviationProcessor._weights.length; i++)
            {
                for (int j = 0; j < standardDeviationProcessor._weights[i].length; j++)
                {
                    error = processor._weights[i][j] - medianProcessor._weights[i][j];
                    standardDeviationProcessor._weights[i][j] += error * error;
                }
            }
        }
        for (int i = 0; i < standardDeviationProcessor._weights.length; i++)
        {
            for (int j = 0; j < standardDeviationProcessor._weights[i].length; j++)
            {
                standardDeviationProcessor._weights[i][j] /= processorsAveraged;
            }
        }
        return standardDeviationProcessor;
    }

    public static NeuralNetwork makeUninitialized()
    {
        return new NeuralNetwork();
    }

    public static NeuralNetwork makeWithProcessorBlueprint(NeuralNetworkBlueprint processorBlueprint)
    {
        return new NeuralNetwork(processorBlueprint.getInputCount(), processorBlueprint.getOutputCount(),
                processorBlueprint.getRandomInitialization(), processorBlueprint.getNormalizeAfterMutation());
    }
}
