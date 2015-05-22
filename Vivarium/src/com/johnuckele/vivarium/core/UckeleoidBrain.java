package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

public class UckeleoidBrain implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long	serialVersionUID				= 4L;

	// Random number state
	private static long _random									= (long)(Math.random()*Long.MAX_VALUE);
	private static long xorShiftLong() {
		_random ^= (_random << 21);
		_random ^= (_random >>> 35);
		_random ^= (_random << 4);
		return _random;
	}

	// World object which we're apart of
	private World _world;
	
	// Weights represents all the weights in the neural network
	// weight[i][j] corresponds to the weight of the connection
	// for the ith node in the layer coming from the jth node in
	// the previous layer.
	// Each node has a two special previous values, a constant
	// bias unit with a value of 1 and a stochastic bias unit
	// with a value between -1 and 1.
	// These extra weights are added after the prior weights
	private double[][][]		_weights;
	private int					_outputCount;
	private double[]			_outputs;
	private int					_inputCount;
	private int					_hiddenLayers;
	private double[][]			_hiddenNodes;

	public UckeleoidBrain(World world, int inputCount, int outputCount, int hiddenLayers)
	{
		this._world = world;
		this._outputCount = outputCount;
		this._inputCount = inputCount;
		this._hiddenLayers = hiddenLayers;
		resetOptimizingDataStructures();
	}

	private void resetOptimizingDataStructures()
	{
		this._outputs = new double[_outputCount];
		this._hiddenNodes = new double[this._hiddenLayers][];
		for(int i = 0; i < this._hiddenLayers; i++)
		{
			this._hiddenNodes[i] = new double[this._inputCount];
		}
		this._weights = new double[this._hiddenLayers + 1][][];
		if(this._hiddenLayers > 0)
		{
			// A NN with at least 1 hidden layer
			this._weights[0] = new double[_inputCount][_inputCount + 2];
			for(int i = 1; i < this._hiddenLayers - 1; i++)
			{
				this._weights[i] = new double[_inputCount][_inputCount + 2];
			}
			this._weights[_hiddenLayers] = new double[_outputCount][_inputCount + 2];

			// With one or more hidden layers, copy inputs to the last layer
			// initially
			// This code assumes that all hidden layers have as many outputs as
			// the overall
			// network has for inputs
			for(int i = 0; i < _weights.length; i++)
			{
				for(int j = 0; j < _weights[i].length; j++)
				{
					_weights[i][j][j] = 1;
				}
			}
			// And then set the weights on the last layer to one
			for(int j = 0; j < _weights[_hiddenLayers].length; j++)
			{
				for(int k = 0; k < _weights[_hiddenLayers][j].length; k++)
				{
					_weights[_hiddenLayers][j][k] = 1;
				}
			}
		}
		else
		{
			// A NN with no hidden layers
			this._weights[0] = new double[_outputCount][_inputCount + 2];
			// With zero hidden layers, set all weights to one
			for(int j = 0; j < _weights[0].length; j++)
			{
				for(int k = 0; k < _weights[0][j].length; k++)
				{
					_weights[0][j][k] = 1;
				}
			}
		}
	}

	public UckeleoidBrain(World world, UckeleoidBrain brain1, UckeleoidBrain brain2)
	{
		// Construct the weight layer and store variables with the int based
		// constructor
		this(world, brain1._inputCount, brain1._outputCount, brain1._hiddenLayers);

		// Set all the weights with
		for(int i = 0; i < _weights.length; i++)
		{
			for(int j = 0; j < _weights[i].length; j++)
			{
				for(int k = 0; k < _weights[i][j].length; k++)
				{
					// Mix first
					double randomValue = Math.random();
					// Sometimes mix the two values with a gaussian
					// approximation.
					if(randomValue < _world.getWorldVariables().getInheritanceGaussianMixRate())
					{
						// Radnom.nextGaussian generates a Gaussian with μ = 0 and σ = 1
						// but we want μ = 0.5 and σ = 0.5 to mix between numbers
						// This can cause a mix to introduce values higher or lower than
						// either parent, which is by design.
						double gaussianRandomValue = World.RANDOM.nextGaussian() / 2 + 0.5;
						double weightDifference = brain2._weights[i][j][k] - brain1._weights[i][j][k];
						_weights[i][j][k] = brain1._weights[i][j][k] + gaussianRandomValue * weightDifference;
					}
					// Otherwise pick one value
					else
					{
						randomValue = Math.random();
						if(randomValue < 0.5)
						{
							_weights[i][j] = brain1._weights[i][j];
						}
						else
						{
							_weights[i][j] = brain2._weights[i][j];
						}
					}

					// Sometimes mutate
					randomValue = Math.random();
					if(randomValue < _world.getWorldVariables().getMutationRate())
					{
						randomValue = Math.random();
						if(randomValue < _world.getWorldVariables().getMutationSmallScaleRate())
						{
							// Gaussian multipliplication mutation,
							// μ = 1 and σ = 0.2
							double gaussianRandomValue = World.RANDOM.nextGaussian() / 5 + 1;
							_weights[i][j][k] = gaussianRandomValue * _weights[i][j][k];
						}
						else
						{
							randomValue -= _world.getWorldVariables().getMutationSmallScaleRate();
							if(randomValue < _world.getWorldVariables().getMutationRandomRate())
							{
								// Random mutation
								_weights[i][j][k] = Math.random() * 2 - 1;
							}
							else
							{
								randomValue -= _world.getWorldVariables().getMutationRandomRate();
								if(randomValue < _world.getWorldVariables().getMutationFlipRate())
								{
									// Flip mutation
									_weights[i][j][k] = -_weights[i][j][k];
								}
								else
								{
									randomValue -= _world.getWorldVariables().getMutationFlipRate();
								}
							}
						}
					}
				}
			}
		}
	}

	public int getOutputCount()
	{
		return(this._outputCount);
	}

	public int getInputCount()
	{
		return(this._inputCount);
	}

	public int getHiddenLayers()
	{
		return(this._hiddenLayers);
	}

	public double[][][] getWeights()
	{
		return(this._weights);
	}

	public void setOutputCount(int outputCount)
	{
		this._outputCount = outputCount;
		resetOptimizingDataStructures();
	}

	public void setInputCount(int inputCount)
	{
		this._inputCount = inputCount;
		resetOptimizingDataStructures();
	}

	public void setHiddenLayers(int hiddenLayers)
	{
		this._hiddenLayers = hiddenLayers;
		resetOptimizingDataStructures();
	}

	public void setWeights(double[][][] weights)
	{
		this._weights = weights;
	}

	public double[] outputs(double[] inputs)
	{
		// Clear the output units
		for(int i = 0; i < _outputCount; i++)
		{
			_outputs[i] = 0;
		}

		// Compute the full NN
		if(_hiddenLayers > 0)
		{
			computeLayerInPlace(inputs, _hiddenNodes[0], _weights[0]);
			for(int i = 1; i < _hiddenNodes.length; i++)
			{
				computeLayerInPlace(_hiddenNodes[i - 1], _hiddenNodes[i], _weights[i]);
			}
			computeLayerInPlace(_hiddenNodes[_hiddenNodes.length - 1], _outputs, _weights[_weights.length - 1]);
		}
		else
		{
			computeLayerInPlace(inputs, _outputs, _weights[0]);
		}

		// Return
		return(_outputs);
	}

	public void computeLayerInPlace(double[] inputs, double[] outputs, double[][] weights)
	{
		for(int i = 0; i < outputs.length; i++)
		{
			// Bias units
			outputs[i] += weights[i][0] * 1;
			outputs[i] += weights[i][1] * UckeleoidBrain.xorShiftLong() / Long.MAX_VALUE;
			// prior units
			for(int j = 0; j < inputs.length; j++)
			{
				outputs[i] += weights[i][j + 2] * inputs[j];
			}
			// Scale for sigmoid
			outputs[i] = UckeleoidBrain.sigmoid(outputs[i]);
		}
	}

	public String toString()
	{
		StringBuffer output = new StringBuffer();

		String[] baseLineEndLabel =
		{ "_", "M", "L", "R", "E", "B" };
		String[] lineEndLabel = new String[this._outputCount];
		for(int i = 0; i < lineEndLabel.length; i++)
		{
			if(i < baseLineEndLabel.length)
			{
				lineEndLabel[i] = baseLineEndLabel[i];
			}
			else
			{
				lineEndLabel[i] = "x"+(i-baseLineEndLabel.length+1);
			}
		}
		String[] baseColumnHeaderLabel =
		{ "cB", "rB", "女", "一", "中", "口", "%" };
		String[] columnHeaderLabel = new String[this._inputCount+2];
		for(int i = 0; i < columnHeaderLabel.length; i++)
		{
			if(i < baseColumnHeaderLabel.length)
			{
				columnHeaderLabel[i] = baseColumnHeaderLabel[i];
			}
			else
			{
				columnHeaderLabel[i] = "x"+(i-baseColumnHeaderLabel.length+1);
			}
		}

		for(int i = 0; i < columnHeaderLabel.length; i++)
		{
			output.append(columnHeaderLabel[i]);
			output.append("\t");
		}
		output.append('\n');

		for(int i = 0; i < _weights.length; i++)
		{
			for(int j = 0; j < _weights[i].length; j++)
			{
				for(int k = 0; k < _weights[i][j].length; k++)
				{
					double weight = _weights[i][j][k];
					output.append(String.format("%.3f", weight));
					output.append('\t');
				}
				if(i == _weights.length - 1)
				{
					output.append(lineEndLabel[j]);
				}
				output.append('\n');
			}
			output.append('\n');
			output.append('\n');
		}

		return(output.toString());
	}

	private static double sigmoid(double x)
	{
		return 1 / (1 + Math.exp(-x));
	}

	public static void main(String[] args)
	{
		System.out.println("Testing UckeleoidBrain code...");
		System.out.println("Calculating Sigmoids...");
		for(double i = -4; i <= 4; i += .5)
		{
			System.out.println("Sigmoid(" + i + ") = " + UckeleoidBrain.sigmoid(i));
		}
		UckeleoidBrain brain = new UckeleoidBrain(null, 2, 7, 0);
		System.out.println("Creating Brain...");
		System.out.println(brain);
		System.out.println("Brain Outputs for inputs");
		double[] inputs =
		{ 0.0, 0.0 };
		System.out.println("" + Arrays.toString(inputs) + " -> " + Arrays.toString(brain.outputs(inputs)));
		System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs)));
		double[] inputs2 =
		{ -1.0, 0.0 };
		System.out.println("" + Arrays.toString(inputs2) + " -> " + Arrays.toString(brain.outputs(inputs2)));
		System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs2)));
		double[] inputs3 =
		{ 1.0, 0.0 };
		System.out.println("" + Arrays.toString(inputs3) + " -> " + Arrays.toString(brain.outputs(inputs3)));
		System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs3)));
		double[] inputs4 =
		{ 0.5, 0.5 };
		System.out.println("" + Arrays.toString(inputs4) + " -> " + Arrays.toString(brain.outputs(inputs4)));
		System.out.println("Maximum output " + Arrays.toString(brain.outputs(inputs4)));

	}

	public static UckeleoidBrain minBrain(LinkedList<UckeleoidBrain> brains)
	{
		UckeleoidBrain minBrain = new UckeleoidBrain(null, brains.get(0)._inputCount, brains.get(0)._outputCount, brains.get(0)._hiddenLayers);
		// Set all the weights with
		for(UckeleoidBrain brain : brains)
		{
			for(int i = 0; i < minBrain._weights.length; i++)
			{
				for(int j = 0; j < minBrain._weights[i].length; j++)
				{
					for(int k = 0; k < minBrain._weights[i][j].length; k++)
					{
						minBrain._weights[i][j][k] = Math.min(brain._weights[i][j][k], minBrain._weights[i][j][k]);
					}
				}
			}
		}
		return minBrain;

	}

	public static UckeleoidBrain maxBrain(LinkedList<UckeleoidBrain> brains)
	{
		UckeleoidBrain maxBrain = new UckeleoidBrain(null, brains.get(0)._inputCount, brains.get(0)._outputCount, brains.get(0)._hiddenLayers);
		// Set all the weights with
		for(UckeleoidBrain brain : brains)
		{
			for(int i = 0; i < maxBrain._weights.length; i++)
			{
				for(int j = 0; j < maxBrain._weights[i].length; j++)
				{
					for(int k = 0; k < maxBrain._weights[i][j].length; k++)
					{
						maxBrain._weights[i][j][k] = Math.max(brain._weights[i][j][k], maxBrain._weights[i][j][k]);
					}
				}
			}
		}
		return maxBrain;
	}

	public static UckeleoidBrain medianBrain(LinkedList<UckeleoidBrain> brains)
	{
		UckeleoidBrain medianBrain = new UckeleoidBrain(null, brains.get(0)._inputCount, brains.get(0)._outputCount, brains.get(0)._hiddenLayers);
		// Set all the weights with
		for(int i = 0; i < medianBrain._weights.length; i++)
		{
			for(int j = 0; j < medianBrain._weights[i].length; j++)
			{
				for(int k = 0; k < medianBrain._weights[i][j].length; k++)
				{
					medianBrain._weights[i][j][k] = 0;
				}
			}
		}
		int brainsAveraged = brains.size();
		for(UckeleoidBrain brain : brains)
		{
			for(int i = 0; i < medianBrain._weights.length; i++)
			{
				for(int j = 0; j < medianBrain._weights[i].length; j++)
				{
					for(int k = 0; k < medianBrain._weights[i][j].length; k++)
					{
						medianBrain._weights[i][j][k] += brain._weights[i][j][k];
					}
				}
			}
		}
		for(int i = 0; i < medianBrain._weights.length; i++)
		{
			for(int j = 0; j < medianBrain._weights[i].length; j++)
			{
				for(int k = 0; k < medianBrain._weights[i][j].length; k++)
				{
					medianBrain._weights[i][j][k] /= brainsAveraged;
				}
			}
		}
		return medianBrain;
	}

	public static UckeleoidBrain standardDeviationBrain(LinkedList<UckeleoidBrain> brains, UckeleoidBrain medianBrain)
	{
		UckeleoidBrain standardDeviationBrain = new UckeleoidBrain(null, medianBrain._inputCount, medianBrain._outputCount, brains.get(0)._hiddenLayers);
		for(int i = 0; i < standardDeviationBrain._weights.length; i++)
		{
			for(int j = 0; j < standardDeviationBrain._weights[i].length; j++)
			{
				for(int k = 0; k < standardDeviationBrain._weights[i][j].length; k++)
				{
					standardDeviationBrain._weights[i][j][k] = 0;
				}
			}
		}
		int brainsAveraged = brains.size();
		double error;
		for(UckeleoidBrain brain : brains)
		{
			for(int i = 0; i < standardDeviationBrain._weights.length; i++)
			{
				for(int j = 0; j < standardDeviationBrain._weights[i].length; j++)
				{
					for(int k = 0; k < standardDeviationBrain._weights[i][j].length; k++)
					{
						error = brain._weights[i][j][k] - medianBrain._weights[i][j][k];
						standardDeviationBrain._weights[i][j][k] += error * error;
					}
				}
			}
		}
		for(int i = 0; i < standardDeviationBrain._weights.length; i++)
		{
			for(int j = 0; j < standardDeviationBrain._weights[i].length; j++)
			{
				for(int k = 0; k < standardDeviationBrain._weights[i][j].length; k++)
				{
					standardDeviationBrain._weights[i][j][k] /= brainsAveraged;
				}
			}
		}
		return standardDeviationBrain;
	}
}
