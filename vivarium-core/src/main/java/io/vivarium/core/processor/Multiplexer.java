package io.vivarium.core.processor;

import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Multiplexer extends VivariumObject
{
    static
    {
        ClassRegistry.getInstance().register(Multiplexer.class);
    }

    private static final int MULTIPLEXER_INPUT = -1;

    @SerializedParameter
    private double[][] _inputs;
    @SerializedParameter
    private double[][] _outputs;
    @SerializedParameter
    private int[][] _source;
    @SerializedParameter
    private int[][] _index;

    private Multiplexer()
    {
    }

    @Override
    public void finalizeSerialization()
    {
    }

    public double[] outputs(double[] inputs, Processor[] processors)
    {
        for (int i = 0; i < _inputs.length; i++)
        {
            for (int j = 0; j < _inputs[i].length; j++)
            {
                if (_source[i][j] == MULTIPLEXER_INPUT)
                {
                    _inputs[i][j] = inputs[_index[i][j]];
                }
                else
                {
                    _inputs[i][j] = _outputs[_source[i][j]][_index[i][j]];
                }
            }
            _outputs[i] = processors[i].outputs(_inputs[i]);
        }
        return processors[0].outputs(inputs);
    }

    public static Multiplexer makeWithSequentialProcessors(int inputCount, int outputCount,
            ProcessorBlueprint[] processorBlueprints)
    {
        Multiplexer m = new Multiplexer();
        m._inputs = new double[processorBlueprints.length][];
        m._outputs = new double[processorBlueprints.length][];
        m._source = new int[processorBlueprints.length][];
        m._index = new int[processorBlueprints.length][];

        m._inputs[0] = new double[inputCount];
        m._source[0] = new int[inputCount];
        m._index[0] = new int[inputCount];
        for (int j = 0; j < inputCount; j++)
        {
            m._source[0][j] = MULTIPLEXER_INPUT;
            m._index[0][j] = j;
        }
        for (int i = 1; i < processorBlueprints.length; i++)
        {
            ProcessorBlueprint blueprint = processorBlueprints[i];
            ProcessorBlueprint preceeding = processorBlueprints[i - 1];
            if (blueprint.getInputCount() != preceeding.getOutputCount())
            {
                throw new IllegalStateException("Mismatched processor sizes in sequential multiplexer!"
                        + " Preceeding processor at index " + (i - 1) + " has " + preceeding.getOutputCount()
                        + " outputs. Current processor at index " + i + " has " + blueprint.getInputCount()
                        + " inputs.");
            }
            m._inputs[i] = new double[blueprint.getInputCount()];
            m._source[i] = new int[blueprint.getInputCount()];
            m._index[i] = new int[blueprint.getInputCount()];
            for (int j = 0; j < blueprint.getInputCount(); j++)
            {
                m._source[i][j] = i - 1;
                m._index[i][j] = j;
            }
        }
        return m;
    }
}
