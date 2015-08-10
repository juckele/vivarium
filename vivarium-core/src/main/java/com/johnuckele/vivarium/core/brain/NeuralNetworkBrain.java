package com.johnuckele.vivarium.core.brain;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;
import com.johnuckele.vivarium.util.Functions;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class NeuralNetworkBrain extends Brain
{
    // Weights represents all the weights in the neural network
    // weight[i][j][k] corresponds to the weight of the connection
    // for the jth node in the layer coming from the kth node in
    // the previous layer.
    // Each node has a two special previous values, a constant
    // bias unit with a value of 1 and a stochastic bias unit
    // with a normally distributed value between -1 and 1.
    private double[][][] _weights;
    private double[]     _outputs;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("weights", double[][][].class, "[[[]]]"));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("outputs", double[].class, "[]"));
    }

    public NeuralNetworkBrain()
    {
    }

    private NeuralNetworkBrain(int inputCount, int outputCount, boolean randomInitialization)
    {
        super();
        assert(inputCount > 0);
        assert(outputCount > 0);
        constructWithDimensions(outputCount, inputCount, randomInitialization);
    }

    private void constructWithDimensions(int inputCount, int outputCount, boolean randomInitialization)
    {
        this._outputs = new double[outputCount];
        this._weights[0] = new double[outputCount][inputCount + 2];
        for (int j = 0; j < _weights[0].length; j++)
        {
            for (int k = 0; k < _weights[0][j].length; k++)
            {
                _weights[0][j][k] = randomInitialization ? Rand.getRandomDouble() : 1;
            }
        }
    }

    public NeuralNetworkBrain(Species species, NeuralNetworkBrain brain1, NeuralNetworkBrain brain2)
    {
        // Construct the weight layer and store variables with the int based
        // constructor
        this(brain1.getInputCount(), brain1.getOutputCount(), false);

        // Set all the weights with
        for (int i = 0; i < _weights.length; i++)
        {
            for (int j = 0; j < _weights[i].length; j++)
            {
                for (int k = 0; k < _weights[i][j].length; k++)
                {
                    // Mix first
                    double randomValue = Rand.getRandomPositiveDouble();
                    // Sometimes mix the two values with a gaussian
                    // approximation.
                    if (randomValue < species.getInheritanceGaussianMixRate())
                    {
                        // Radnom.nextGaussian generates a Gaussian with μ = 0
                        // and σ = 1
                        // but we want μ = 0.5 and σ = 0.5 to mix between
                        // numbers
                        // This can cause a mix to introduce values higher or
                        // lower than
                        // either parent, which is by design.
                        double gaussianRandomValue = Rand.getRandomGaussian() / 2 + 0.5;
                        double weightDifference = brain2._weights[i][j][k] - brain1._weights[i][j][k];
                        _weights[i][j][k] = brain1._weights[i][j][k] + gaussianRandomValue * weightDifference;
                    }
                    // Otherwise pick one value
                    else
                    {
                        randomValue = Rand.getRandomPositiveDouble();
                        if (randomValue < 0.5)
                        {
                            _weights[i][j] = brain1._weights[i][j];
                        }
                        else
                        {
                            _weights[i][j] = brain2._weights[i][j];
                        }
                    }

                    // Sometimes mutate
                    randomValue = Rand.getRandomPositiveDouble();
                    if (randomValue < species.getMutationRate())
                    {
                        randomValue = Rand.getRandomPositiveDouble();
                        if (randomValue < species.getMutationSmallScaleRate())
                        {
                            // Gaussian multiplication mutation,
                            // μ = 1 and σ = 0.2
                            double gaussianRandomValue = Rand.getRandomGaussian() / 5 + 1;
                            _weights[i][j][k] = gaussianRandomValue * _weights[i][j][k];
                        }
                        else
                        {
                            randomValue -= species.getMutationSmallScaleRate();
                            if (randomValue < species.getMutationRandomRate())
                            {
                                // Random mutation
                                _weights[i][j][k] = Rand.getRandomDouble();
                            }
                            else
                            {
                                randomValue -= species.getMutationRandomRate();
                                if (randomValue < species.getMutationFlipRate())
                                {
                                    // Flip mutation
                                    _weights[i][j][k] = -_weights[i][j][k];
                                }
                                else
                                {
                                    randomValue -= species.getMutationFlipRate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public double[][][] getWeights()
    {
        return (this._weights);
    }

    public int getInputCount()
    {
        return _weights.length;
    }

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
        if (_weights.length > 0)
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
            outputs[i] += weights[i][1] * Rand.getRandomDouble();
            // prior units
            for (int j = 0; j < inputs.length; j++)
            {
                outputs[i] += weights[i][j + 2] * inputs[j];
            }
            // Scale for sigmoid
            outputs[i] = Functions.sigmoid(outputs[i]);
        }
    }

    @Override
    public String render(RenderCode code)
    {
        if (code == RenderCode.BRAIN_WEIGHTS)
        {
            return this.renderBrainWeights();
        }
        else
        {
            throw new IllegalArgumentException("RenderCode " + code + " not supported for type " + this.getClass());
        }
    }

    private String renderBrainWeights()
    {
        StringBuffer output = new StringBuffer();

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
                    double weight = _weights[i][j][k];
                    output.append(weight);
                    output.append('\t');
                }
                if (i == _weights.length - 1)
                {
                    output.append(lineEndLabel[j]);
                }
                output.append('\n');
            }
        }

        return (output.toString());
    }

    public static void main(String[] args)
    {
        NeuralNetworkBrain brain = new NeuralNetworkBrain(3, 10, false);
        System.out.println("Creating Brain...");
        System.out.println(brain);
        System.out.println("Brain Outputs for inputs");
        double[] inputs = { 0.0, 0.0 };
        System.out.println("" + Arrays.toString(inputs) + " -> " + Arrays.toString(brain.outputs(inputs)));
        System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs)));
        double[] inputs2 = { -1.0, 0.0 };
        System.out.println("" + Arrays.toString(inputs2) + " -> " + Arrays.toString(brain.outputs(inputs2)));
        System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs2)));
        double[] inputs3 = { 1.0, 0.0 };
        System.out.println("" + Arrays.toString(inputs3) + " -> " + Arrays.toString(brain.outputs(inputs3)));
        System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs3)));
        double[] inputs4 = { 0.5, 0.5 };
        System.out.println("" + Arrays.toString(inputs4) + " -> " + Arrays.toString(brain.outputs(inputs4)));
        System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs4)));

    }

    public static NeuralNetworkBrain minBrain(List<NeuralNetworkBrain> brains)
    {
        NeuralNetworkBrain minBrain = new NeuralNetworkBrain(brains.get(0).getInputCount(),
                brains.get(0).getOutputCount(), false);
        // Set all the weights with
        for (NeuralNetworkBrain brain : brains)
        {
            for (int i = 0; i < minBrain._weights.length; i++)
            {
                for (int j = 0; j < minBrain._weights[i].length; j++)
                {
                    for (int k = 0; k < minBrain._weights[i][j].length; k++)
                    {
                        minBrain._weights[i][j][k] = Math.min(brain._weights[i][j][k], minBrain._weights[i][j][k]);
                    }
                }
            }
        }
        return minBrain;

    }

    public static NeuralNetworkBrain maxBrain(List<NeuralNetworkBrain> brains)
    {
        NeuralNetworkBrain maxBrain = new NeuralNetworkBrain(brains.get(0).getInputCount(),
                brains.get(0).getOutputCount(), false);
        // Set all the weights with
        for (NeuralNetworkBrain brain : brains)
        {
            for (int i = 0; i < maxBrain._weights.length; i++)
            {
                for (int j = 0; j < maxBrain._weights[i].length; j++)
                {
                    for (int k = 0; k < maxBrain._weights[i][j].length; k++)
                    {
                        maxBrain._weights[i][j][k] = Math.max(brain._weights[i][j][k], maxBrain._weights[i][j][k]);
                    }
                }
            }
        }
        return maxBrain;
    }

    public static NeuralNetworkBrain medianBrain(List<NeuralNetworkBrain> brains)
    {
        NeuralNetworkBrain medianBrain = new NeuralNetworkBrain(brains.get(0).getInputCount(),
                brains.get(0).getOutputCount(), false);
        // Set all the weights with
        for (int i = 0; i < medianBrain._weights.length; i++)
        {
            for (int j = 0; j < medianBrain._weights[i].length; j++)
            {
                for (int k = 0; k < medianBrain._weights[i][j].length; k++)
                {
                    medianBrain._weights[i][j][k] = 0;
                }
            }
        }
        int brainsAveraged = brains.size();
        for (NeuralNetworkBrain brain : brains)
        {
            for (int i = 0; i < medianBrain._weights.length; i++)
            {
                for (int j = 0; j < medianBrain._weights[i].length; j++)
                {
                    for (int k = 0; k < medianBrain._weights[i][j].length; k++)
                    {
                        medianBrain._weights[i][j][k] += brain._weights[i][j][k];
                    }
                }
            }
        }
        for (int i = 0; i < medianBrain._weights.length; i++)
        {
            for (int j = 0; j < medianBrain._weights[i].length; j++)
            {
                for (int k = 0; k < medianBrain._weights[i][j].length; k++)
                {
                    medianBrain._weights[i][j][k] /= brainsAveraged;
                }
            }
        }
        return medianBrain;
    }

    public static NeuralNetworkBrain standardDeviationBrain(List<NeuralNetworkBrain> brains,
            NeuralNetworkBrain medianBrain)
    {
        NeuralNetworkBrain standardDeviationBrain = new NeuralNetworkBrain(medianBrain.getInputCount(),
                medianBrain.getOutputCount(), false);
        for (int i = 0; i < standardDeviationBrain._weights.length; i++)
        {
            for (int j = 0; j < standardDeviationBrain._weights[i].length; j++)
            {
                for (int k = 0; k < standardDeviationBrain._weights[i][j].length; k++)
                {
                    standardDeviationBrain._weights[i][j][k] = 0;
                }
            }
        }
        int brainsAveraged = brains.size();
        double error;
        for (NeuralNetworkBrain brain : brains)
        {
            for (int i = 0; i < standardDeviationBrain._weights.length; i++)
            {
                for (int j = 0; j < standardDeviationBrain._weights[i].length; j++)
                {
                    for (int k = 0; k < standardDeviationBrain._weights[i][j].length; k++)
                    {
                        error = brain._weights[i][j][k] - medianBrain._weights[i][j][k];
                        standardDeviationBrain._weights[i][j][k] += error * error;
                    }
                }
            }
        }
        for (int i = 0; i < standardDeviationBrain._weights.length; i++)
        {
            for (int j = 0; j < standardDeviationBrain._weights[i].length; j++)
            {
                for (int k = 0; k < standardDeviationBrain._weights[i][j].length; k++)
                {
                    standardDeviationBrain._weights[i][j][k] /= brainsAveraged;
                }
            }
        }
        return standardDeviationBrain;
    }

    @Override
    public BrainType getBrainType()
    {
        return BrainType.NEURAL_NETWORK;
    }

    @Override
    public List<MapSerializer> getReferences()
    {
        return new LinkedList<MapSerializer>();
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return NeuralNetworkBrain.SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "outputs":
                return this._outputs;
            case "weights":
                return this._weights;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "outputs":
                this._outputs = (double[]) value;
                break;
            case "weights":
                this._weights = (double[][][]) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    public static NeuralNetworkBrain makeUninitialized()
    {
        return new NeuralNetworkBrain();
    }

    public static NeuralNetworkBrain makeDefault()
    {
        NeuralNetworkBrain brain = new NeuralNetworkBrain();
        new SerializationEngine().deserialize(brain, SerializationEngine.EMPTY_OBJECT_MAP);
        return brain;
    }

    public static NeuralNetworkBrain makeCopy(NeuralNetworkBrain original)
    {
        return (NeuralNetworkBrain) new SerializationEngine().makeCopy(original);
    }

    public static NeuralNetworkBrain makeWithSpecies(Species species)
    {
        return new NeuralNetworkBrain(species.getTotalBrainInputCount(), species.getTotalBrainOutputCount(),
                species.getRandomInitialization());
    }

    public static NeuralNetworkBrain makeWithParents(Species species, NeuralNetworkBrain parentBrain1,
            NeuralNetworkBrain parentBrain2)
    {
        return new NeuralNetworkBrain(species, parentBrain1, parentBrain2);
    }

    public static NeuralNetworkBrain makeWithDimensions(int inputCount, int outputCount, boolean b)
    {
        return new NeuralNetworkBrain(inputCount, outputCount, b);
    }
}
