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

    // Initialization
    @SerializedParameter
    private boolean _randomInitialization = false;

    // Normalization
    @SerializedParameter
    private double _normalizeAfterMutation = 0;

    protected ProcessorBlueprint()
    {
    }

    public abstract Processor makeProcessor(int inputs, int outputs);

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

    public double getNormalizeAfterMutation()
    {
        return this._normalizeAfterMutation;
    }

    public void setNormalizeAfterMutation(double normalizeAfterMutation)
    {
        _normalizeAfterMutation = normalizeAfterMutation;
    }

    public static ProcessorBlueprint makeDefault()
    {
        return NeuralNetworkBlueprint.makeDefault();
    }
}
