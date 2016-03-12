package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class ProcessorArchitecture extends VivariumObject
{
    @SerializedParameter
    private ProcessorType _processorType = ProcessorType.NEURAL_NETWORK;
    @SerializedParameter
    private boolean _randomInitialization = false;
    @SerializedParameter
    private double _normalizeAfterMutation = 0;

    protected ProcessorArchitecture()
    {
    }

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

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    public static ProcessorArchitecture makeDefault()
    {
        ProcessorArchitecture a = new ProcessorArchitecture();
        a.finalizeSerialization();
        return a;
    }
}
