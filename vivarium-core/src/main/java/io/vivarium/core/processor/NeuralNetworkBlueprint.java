package io.vivarium.core.processor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class NeuralNetworkBlueprint extends ProcessorBlueprint
{
    private NeuralNetworkBlueprint()
    {
    }

    @Override
    public NeuralNetwork makeProcessor()
    {
        return NeuralNetwork.makeWithProcessorBlueprint(this);
    }

    public static NeuralNetworkBlueprint makeDefault()
    {
        NeuralNetworkBlueprint a = new NeuralNetworkBlueprint();
        a.finalizeSerialization();
        return a;
    }
}
