package io.vivarium.core.processor;

import io.vivarium.core.RenderCode;
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

    public static void main(String[] args)
    {
        NeuralNetworkBlueprint blueprint = makeDefault();
        blueprint.setHiddenLayerCount(1);
        System.out.println(blueprint);
        NeuralNetwork processor = blueprint.makeProcessor();
        System.out.println(processor);
        System.out.println(processor.render(RenderCode.PROCESSOR_WEIGHTS));

    }
}
