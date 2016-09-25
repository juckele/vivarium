package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class DecisionTree extends Processor
{
    @SerializedParameter
    private int _inputCount;
    @SerializedParameter
    private double[] _outputs;

    @SerializedParameter
    private double[] _thresholds;
    @SerializedParameter
    private int[] _indices;

    private DecisionTree(DecisionTreeBlueprint blueprint, DecisionTree parentProcessor1, DecisionTree parentProcessor2)
    {
        this._inputCount = parentProcessor1._inputCount;
        this._outputs = new double[parentProcessor1._outputs.length];
        int nodeCount = parentProcessor1._thresholds.length;
        this._thresholds = new double[nodeCount];
        this._indices = new int[nodeCount];

        for (int i = 0; i < nodeCount; i++)
        {
            // Internal node
            if (i < nodeCount / 2)
            {
                double randomValue = Rand.getInstance().getRandomPositiveDouble();
                if (randomValue < blueprint.getMutationRate())
                {
                    this._thresholds[i] = Rand.getInstance().getRandomPositiveDouble();
                    this._indices[i] = Rand.getInstance().getRandomInt(_inputCount);
                }
                else
                {
                    randomValue = Rand.getInstance().getRandomPositiveDouble();
                    if (randomValue < 0.5)
                    {
                        this._thresholds[i] = parentProcessor1._thresholds[i];
                        this._indices[i] = parentProcessor1._indices[i];
                    }
                    else
                    {
                        this._thresholds[i] = parentProcessor2._thresholds[i];
                        this._indices[i] = parentProcessor2._indices[i];

                    }
                }
            }
            // Leaf node
            else
            {
                double randomValue = Rand.getInstance().getRandomPositiveDouble();
                if (randomValue < blueprint.getMutationRate())
                {
                    this._indices[i] = Integer.MIN_VALUE + Rand.getInstance().getRandomInt(_outputs.length);
                }
                else
                {
                    randomValue = Rand.getInstance().getRandomPositiveDouble();
                    if (randomValue < 0.5)
                    {
                        this._indices[i] = parentProcessor1._indices[i];
                    }
                    else
                    {
                        this._indices[i] = parentProcessor2._indices[i];

                    }
                }
            }
        }
    }

    public DecisionTree(int maximumDepth, int inputCount, int outputCount)
    {
        this._inputCount = inputCount;
        this._outputs = new double[outputCount];
        int nodeCount = (2 << maximumDepth - 1) - 1;
        this._thresholds = new double[nodeCount];
        this._indices = new int[nodeCount];

        for (int i = 0; i < nodeCount; i++)
        {
            // Internal node
            if (i < nodeCount / 2)
            {
                this._thresholds[i] = Rand.getInstance().getRandomPositiveDouble();
                this._indices[i] = Rand.getInstance().getRandomInt(_inputCount);
            }
            // Leaf node
            else
            {
                this._indices[i] = Integer.MIN_VALUE + Rand.getInstance().getRandomInt(_outputs.length);
            }
        }
    }

    private DecisionTree()
    {
    }

    @Override
    public double[] outputs(double[] inputs)
    {
        zeroOutputs();
        decideNode(inputs, 0);
        return _outputs;
    }

    @Override
    public double[] outputs()
    {
        return _outputs;
    }

    private void zeroOutputs()
    {
        for (int i = 0; i < _outputs.length; i++)
        {
            _outputs[i] = 0;
        }
    }

    private void decideNode(double[] inputs, int nodeIndex)
    {
        // Check if the node is a leaf or not.
        // Leaf indices are negative.
        if (_indices[nodeIndex] < 0)
        {
            // Set the output based on the leaf we reached
            int output = _indices[nodeIndex] + Integer.MIN_VALUE;
            _outputs[output] = 1;
        }
        else
        {
            // Recur down the tree
            double input = inputs[_indices[nodeIndex]];
            if (input < _thresholds[nodeIndex])
            {
                decideNode(inputs, 2 * nodeIndex + 1);
            }
            else
            {
                decideNode(inputs, 2 * nodeIndex + 2);
            }
        }
    }

    @Override
    public int getInputCount()
    {
        return _inputCount;
    }

    @Override
    public int getOutputCount()
    {
        return _outputs.length;
    }

    public double[] getThresholds()
    {
        return _thresholds;
    }

    public int[] getIndices()
    {
        return _indices;
    }

    public static DecisionTree makeUninitialized()
    {
        return new DecisionTree();
    }

    public static DecisionTree makeWithProcessorBlueprint(DecisionTreeBlueprint decisionTreeBlueprint)
    {
        return new DecisionTree(decisionTreeBlueprint.getMaximumDepth(), decisionTreeBlueprint.getInputCount(),
                decisionTreeBlueprint.getOutputCount());
    }

    public static DecisionTree makeProcessorWithParents(DecisionTreeBlueprint decisionTreeBlueprint,
            Processor untypedProcessor1, Processor untypedProcessor2)
    {
        DecisionTree parent1 = (DecisionTree) untypedProcessor1;
        DecisionTree parent2 = (DecisionTree) untypedProcessor2;
        return new DecisionTree(decisionTreeBlueprint, parent1, parent2);
    }
}
