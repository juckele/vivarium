package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class NeuralNetworkBlueprint extends ProcessorBlueprint
{
    // Mutation
    @SerializedParameter
    private double _inheritanceGaussianMixRate = 0.8;
    @SerializedParameter
    private double _mutationRateExponent = -7;
    private double _mutationRate;
    @SerializedParameter
    private double _mutationTypeSmallScaleRate = 0.5;
    @SerializedParameter
    private double _mutationTypeRandomRate = 0.25;
    @SerializedParameter
    private double _mutationTypeFlipRate = 0.25;

    // NN Structure
    @SerializedParameter
    private int _hiddenLayerCount = 0;

    private NeuralNetworkBlueprint()
    {
    }

    public double getMutationSmallScaleRate()
    {
        return this._mutationTypeSmallScaleRate;
    }

    public double getMutationRateExponent()
    {
        return this._mutationRateExponent;
    }

    public double getMutationRate()
    {
        return this._mutationRate;
    }

    public double getMutationFlipRate()
    {
        return this._mutationTypeFlipRate;
    }

    public double getMutationRandomRate()
    {
        return this._mutationTypeRandomRate;
    }

    public double getInheritanceGaussianMixRate()
    {
        return this._inheritanceGaussianMixRate;
    }

    public void setMutationRateExponent(double exponent)
    {
        this._mutationRateExponent = exponent;
        this._mutationRate = Math.pow(2, exponent);
    }

    public int getHiddenLayerCount()
    {
        return _hiddenLayerCount;
    }

    public void setHiddenLayerCount(int hiddenLayerCount)
    {
        this._hiddenLayerCount = hiddenLayerCount;
    }

    @Override
    public void finalizeSerialization()
    {
        // update mutation rate
        _mutationRate = Math.pow(2, _mutationRateExponent);
    }

    @Override
    public NeuralNetwork makeProcessor(int inputs, int outputs)
    {
        return NeuralNetwork.makeWithProcessorBlueprint(this, inputs, outputs);
    }

    @Override
    public NeuralNetwork makeProcessorWithParents(Processor parent1, Processor parent2)
    {
        return new NeuralNetwork(this, (NeuralNetwork) parent1, (NeuralNetwork) parent2);
    }

    public static NeuralNetworkBlueprint makeDefault()
    {
        NeuralNetworkBlueprint a = new NeuralNetworkBlueprint();
        a.finalizeSerialization();
        return a;
    }
}
