package io.vivarium.core.processor;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Functions;
import io.vivarium.util.Rand;
import io.vivarium.visualization.RenderCode;
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
    private double[][][] _weights;
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
        this._weights = new double[1][][]; // Store all NNs as multi-layer with no hidden layer for now
        this._weights[0] = new double[outputCount][inputCount + BIAS_UNIT_COUNT];
        for (int j = 0; j < _weights[0].length; j++)
        {
            for (int k = 0; k < _weights[0][j].length; k++)
            {
                _weights[0][j][k] = randomInitialization ? Rand.getInstance().getRandomDouble() : 1;
            }
        }
        if (normalizedLength != 0)
        {
            normalizeWeights(normalizedLength);
        }
    }

    public NeuralNetwork(CreatureBlueprint creatureBlueprint, NeuralNetwork processor1, NeuralNetwork processor2)
    {
        // Construct the weight layer and store variables with the int based
        // constructor
        this(processor1.getInputCount(), processor1.getOutputCount(), false, creatureBlueprint.getNormalizeAfterMutation());

        // Set all the weights with
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                for (int k = 0; k < _weights[i][j].length; k++)
                {
                    // Mix first
                    double randomValue = Rand.getInstance().getRandomPositiveDouble();
                    // Sometimes mix the two values with a gaussian
                    // approximation.
                    if (randomValue < creatureBlueprint.getInheritanceGaussianMixRate())
                    {
                        // Radnom.nextGaussian generates a Gaussian with μ = 0
                        // and σ = 1
                        // but we want μ = 0.5 and σ = 0.5 to mix between
                        // numbers
                        // This can cause a mix to introduce values higher or
                        // lower than
                        // either parent, which is by design.
                        double gaussianRandomValue = Rand.getInstance().getRandomGaussian() / 2 + 0.5;
                        double weightDifference = processor2._weights[i][j][k] - processor1._weights[i][j][k];
                        _weights[i][j][k] = processor1._weights[i][j][k] + gaussianRandomValue * weightDifference;
                    }
                    // Otherwise pick one value
                    else
                    {
                        randomValue = Rand.getInstance().getRandomPositiveDouble();
                        if (randomValue < 0.5)
                        {
                            _weights[i][j][k] = processor1._weights[i][j][k];
                        }

                        else
                        {
                            _weights[i][j][k] = processor2._weights[i][j][k];
                        }
                    }

                    // Sometimes mutate
                    randomValue = Rand.getInstance().getRandomPositiveDouble();
                    if (randomValue < creatureBlueprint.getMutationRate())
                    {
                        randomValue = Rand.getInstance().getRandomPositiveDouble();
                        if (randomValue < creatureBlueprint.getMutationSmallScaleRate())
                        {
                            // Gaussian multiplication mutation,
                            // μ = 1 and σ = 0.2
                            double gaussianRandomValue = Rand.getInstance().getRandomGaussian() / 5 + 1;
                            _weights[i][j][k] = gaussianRandomValue * _weights[i][j][k];
                        }
                        else
                        {
                            randomValue -= creatureBlueprint.getMutationSmallScaleRate();
                            if (randomValue < creatureBlueprint.getMutationRandomRate())
                            {
                                // Random mutation
                                _weights[i][j][k] = Rand.getInstance().getRandomDouble();
                            }
                            else
                            {
                                randomValue -= creatureBlueprint.getMutationRandomRate();
                                if (randomValue < creatureBlueprint.getMutationFlipRate())
                                {
                                    // Flip mutation
                                    _weights[i][j][k] = -_weights[i][j][k];
                                }
                                else
                                {
                                    randomValue -= creatureBlueprint.getMutationFlipRate();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (creatureBlueprint.getNormalizeAfterMutation() != 0)
        {
            normalizeWeights(creatureBlueprint.getNormalizeAfterMutation());
        }
    }

    @Override
    public void normalizeWeights(double normalizedLength)
    {
        double vectorLength = getGenomeLength();
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                for (int k = 0; k < _weights[i][j].length; k++)
                {
                    _weights[i][j][k] = normalizedLength * _weights[i][j][k] / vectorLength;
                }
            }
        }
    }

    @Override
    public double getGenomeLength()
    {
        double sumOfSquares = 0;
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                for (int k = 0; k < _weights[i][j].length; k++)
                {
                    sumOfSquares += Math.pow(_weights[i][j][k], 2);
                }
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    public double[][][] getWeights()
    {
        return (this._weights);
    }

    @Override
    public int getInputCount()
    {
        return _weights[0][0].length - BIAS_UNIT_COUNT;
    }

    @Override
    public int getOutputCount()
    {
        return _weights[0].length;
    }

    @Override
    public double[] outputs(double[] inputs)
    {
        // Clear the output units
        for (int i = 0; i < _outputs.length; i++)
        {
            _outputs[i] = 0;
        }

        // Compute the full NN
        if (_weights.length != 1)
        {
            throw new UnsupportedOperationException("Hidden layer NNs are currently not fully implemented.");
        }
        else
        {
            computeLayerInPlace(inputs, _outputs, _weights[0]);
        }

        // Return
        return (_outputs);
    }

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

    @Override
    public String render(RenderCode code)
    {
        if (code == RenderCode.PROCESSOR_WEIGHTS)
        {
            return this.renderProcessorWeights();
        }
        else
        {
            throw new IllegalArgumentException("RenderCode " + code + " not supported for type " + this.getClass());
        }
    }

    private String renderProcessorWeights()
    {
        StringBuilder output = new StringBuilder();

        String[] baseLineEndLabel = { "_", "M", "L", "R", "E", "B" };
        String[] lineEndLabel = new String[this.getOutputCount()];
        for (int i = 0; i < lineEndLabel.length; i++)
        {
            if (i < baseLineEndLabel.length)
            {
                lineEndLabel[i] = baseLineEndLabel[i];
            }
            else
            {
                lineEndLabel[i] = "x" + (i - baseLineEndLabel.length + 1);
            }
        }
        String[] baseColumnHeaderLabel = { "cB", "rB", "女", "一", "中", "口", "%" };
        String[] columnHeaderLabel = new String[this.getInputCount() + 2];
        for (int i = 0; i < columnHeaderLabel.length; i++)
        {
            if (i < baseColumnHeaderLabel.length)
            {
                columnHeaderLabel[i] = baseColumnHeaderLabel[i];
            }
            else
            {
                columnHeaderLabel[i] = "x" + (i - baseColumnHeaderLabel.length + 1);
            }
        }

        for (int i = 0; i < columnHeaderLabel.length; i++)
        {
            output.append(columnHeaderLabel[i]);
            output.append("\t");
        }
        output.append('\n');

        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                for (int k = 0; k < _weights[i][j].length; k++)
                {
                    double weight = (int) (1000 * _weights[i][j][k]) / 1000.0;
                    output.append(weight);
                    output.append('\t');
                }
                output.append(lineEndLabel[j]);
                output.append('\n');
            }
        }

        return (output.toString());
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
                    for (int k = 0; k < minProcessor._weights[i][j].length; k++)
                    {
                        minProcessor._weights[i][j][k] = Math.min(processor._weights[i][j][k],
                                minProcessor._weights[i][j][k]);
                    }
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
                    for (int k = 0; k < maxProcessor._weights[i][j].length; k++)
                    {
                        maxProcessor._weights[i][j][k] = Math.max(processor._weights[i][j][k],
                                maxProcessor._weights[i][j][k]);
                    }
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
                for (int k = 0; k < medianProcessor._weights[i][j].length; k++)
                {
                    medianProcessor._weights[i][j][k] = 0;
                }
            }
        }
        int processorsAveraged = processors.size();
        for (NeuralNetwork processor : processors)
        {
            for (int i = 0; i < medianProcessor._weights.length; i++)
            {
                for (int j = 0; j < medianProcessor._weights[i].length; j++)
                {
                    for (int k = 0; k < medianProcessor._weights[i][j].length; k++)
                    {
                        medianProcessor._weights[i][j][k] += processor._weights[i][j][k];
                    }
                }
            }
        }
        for (int i = 0; i < medianProcessor._weights.length; i++)
        {
            for (int j = 0; j < medianProcessor._weights[i].length; j++)
            {
                for (int k = 0; k < medianProcessor._weights[i][j].length; k++)
                {
                    medianProcessor._weights[i][j][k] /= processorsAveraged;
                }
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
                for (int k = 0; k < standardDeviationProcessor._weights[i][j].length; k++)
                {
                    standardDeviationProcessor._weights[i][j][k] = 0;
                }
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
                    for (int k = 0; k < standardDeviationProcessor._weights[i][j].length; k++)
                    {
                        error = processor._weights[i][j][k] - medianProcessor._weights[i][j][k];
                        standardDeviationProcessor._weights[i][j][k] += error * error;
                    }
                }
            }
        }
        for (int i = 0; i < standardDeviationProcessor._weights.length; i++)
        {
            for (int j = 0; j < standardDeviationProcessor._weights[i].length; j++)
            {
                for (int k = 0; k < standardDeviationProcessor._weights[i][j].length; k++)
                {
                    standardDeviationProcessor._weights[i][j][k] /= processorsAveraged;
                }
            }
        }
        return standardDeviationProcessor;
    }

    @Override
    public ProcessorType getProcessorType()
    {
        return ProcessorType.NEURAL_NETWORK;
    }

    public static NeuralNetwork makeUninitialized()
    {
        return new NeuralNetwork();
    }

    public static NeuralNetwork makeWithCreatureBlueprint(CreatureBlueprint creatureBlueprint)
    {
        return new NeuralNetwork(creatureBlueprint.getTotalProcessorInputCount(), creatureBlueprint.getTotalProcessorOutputCount(),
                creatureBlueprint.getProcessorBlueprint().getRandomInitialization(), creatureBlueprint.getNormalizeAfterMutation());
    }

    public static NeuralNetwork makeWithParents(CreatureBlueprint creatureBlueprint, NeuralNetwork parentProcessor1,
            NeuralNetwork parentProcessor2)
    {
        return new NeuralNetwork(creatureBlueprint, parentProcessor1, parentProcessor2);
    }
}
