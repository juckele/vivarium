package io.vivarium.core.processor;

import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class DecisionTreeBlueprint extends ProcessorBlueprint
{
    // Mutation
    @SerializedParameter
    private double _mutationRateExponent = -7;
    private double _mutationRate;

    // Decision tree structure
    @SerializedParameter
    private int _maximumDepth = 4;

    private DecisionTreeBlueprint()
    {
        super(0, 0);
    }

    private DecisionTreeBlueprint(int inputCount, int outputCount)
    {
        super(inputCount, outputCount);
    }

    public int getMaximumDepth()
    {
        return _maximumDepth;
    }

    public void setMaximumDepth(int maximumDepth)
    {
        this._maximumDepth = maximumDepth;
    }

    public double getMutationRateExponent()
    {
        return this._mutationRateExponent;
    }

    public double getMutationRate()
    {
        return this._mutationRate;
    }

    public void setMutationRateExponent(double exponent)
    {
        this._mutationRateExponent = exponent;
        this._mutationRate = Math.pow(2, exponent);
    }

    @Override
    public void finalizeSerialization()
    {
        // update mutation rate
        _mutationRate = Math.pow(2, _mutationRateExponent);
    }

    @Override
    public DecisionTree makeProcessor()
    {
        return DecisionTree.makeWithProcessorBlueprint(this);
    }

    @Override
    public DecisionTree makeProcessorWithParents(Processor parent1, Processor parent2)
    {
        return DecisionTree.makeProcessorWithParents(this, parent1, parent2);
    }

    public static DecisionTreeBlueprint makeDefault(int inputCount, int outputCount)
    {
        DecisionTreeBlueprint a = new DecisionTreeBlueprint(inputCount, outputCount);
        a.finalizeSerialization();
        return a;
    }
}
