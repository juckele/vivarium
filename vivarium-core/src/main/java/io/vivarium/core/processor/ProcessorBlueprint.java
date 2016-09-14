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
    private int _inputCount;
    @SerializedParameter
    private int _outputCount;

    // Initialization
    @SerializedParameter
    private boolean _randomInitialization = false;

    @SuppressWarnings("unused")
    private ProcessorBlueprint()
    {
    }

    protected ProcessorBlueprint(int inputCount, int outputCount)
    {
        _inputCount = inputCount;
        _outputCount = outputCount;
    }

    public abstract Processor makeProcessor();

    public abstract Processor makeProcessorWithParents(Processor parent1, Processor parent2);

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

    public static ProcessorBlueprint makeDefault(int inputCount, int outputCount)
    {
        ProcessorBlueprint b = NeuralNetworkBlueprint.makeDefault(inputCount, outputCount);
        return b;
    }

    public int getInputCount()
    {
        return _inputCount;
    }

    public int getOutputCount()
    {
        return _outputCount;
    }
}
