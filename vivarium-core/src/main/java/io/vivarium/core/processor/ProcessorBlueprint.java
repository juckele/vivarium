package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class ProcessorBlueprint extends VivariumObject
{
    @SerializedParameter
    private ProcessorType _processorType = ProcessorType.NEURAL_NETWORK;

    @SerializedParameter
    private boolean _randomInitialization = false;
    @SerializedParameter
    private double _normalizeAfterMutation = 0;
    @SerializedParameter

    private int _sensorInputCount = 5;
    @SerializedParameter
    private int _controllerOutputCount = 6;
    @SerializedParameter
    private int _memoryUnitCount = 0;
    @SerializedParameter
    private int _soundChannelCount = 0;
    @SerializedParameter
    private int _signChannelCount = 0;

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

    protected ProcessorBlueprint()
    {
    }

    public abstract Processor makeProcessor();

    public ProcessorType getProcessorType()
    {
        return this._processorType;
    }

    public void setProcessorType(ProcessorType type)
    {
        this._processorType = type;
    }

    public void setRandomInitialization(boolean b)
    {
        this._randomInitialization = b;
    }

    public boolean getRandomInitialization()
    {
        return this._randomInitialization;
    }

    public int getHardProcessorInputs()
    {
        return _sensorInputCount;
    }

    public int getHardProcessorOutputs()
    {
        return _controllerOutputCount;
    }

    public double getNormalizeAfterMutation()
    {
        return this._normalizeAfterMutation;
    }

    public void setNormalizeAfterMutation(double normalizeAfterMutation)
    {
        _normalizeAfterMutation = normalizeAfterMutation;
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

    public int getMemoryUnitCount()
    {
        return this._memoryUnitCount;
    }

    public int getSoundChannelCount()
    {
        return this._soundChannelCount;
    }

    public int getSignChannelCount()
    {
        return this._signChannelCount;
    }

    public void setMutationRateExponent(double exponent)
    {
        this._mutationRateExponent = exponent;
        this._mutationRate = Math.pow(2, exponent);
    }

    public void setCreatureMemoryUnitCount(int memoryUnitCount)
    {
        this._memoryUnitCount = memoryUnitCount;
    }

    public void setCreatureSoundChannelCount(int soundChannelCount)
    {
        this._soundChannelCount = soundChannelCount;
    }

    public void setCreatureSignChannelCount(int signChannelCount)
    {
        this._signChannelCount = signChannelCount;
    }

    public int getTotalProcessorInputCount()
    {
        return this._sensorInputCount + this._memoryUnitCount + this._soundChannelCount + this._signChannelCount;
    }

    public int getTotalProcessorOutputCount()
    {
        return this._controllerOutputCount + this._memoryUnitCount + this._soundChannelCount + this._signChannelCount;
    }

    @Override
    public void finalizeSerialization()
    {
        // update mutation rate
        _mutationRate = Math.pow(2, _mutationRateExponent);
    }

    public static ProcessorBlueprint makeDefault()
    {
        return NeuralNetworkBlueprint.makeDefault();
    }
}
