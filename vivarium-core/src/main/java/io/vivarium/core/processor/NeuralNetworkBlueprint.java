package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class NeuralNetworkBlueprint extends ProcessorBlueprint
{
    @SerializedParameter
    private int _hiddenLayerCount = 0;

    private NeuralNetworkBlueprint()
    {
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
    public NeuralNetwork makeProcessor(int inputs, int outputs)
    {
        return NeuralNetwork.makeWithProcessorBlueprint(this, inputs, outputs);
    }

    public static NeuralNetworkBlueprint makeDefault()
    {
        NeuralNetworkBlueprint a = new NeuralNetworkBlueprint();
        a.finalizeSerialization();
        return a;
    }
}
