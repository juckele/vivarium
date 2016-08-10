package io.vivarium.core;

import io.vivarium.core.processor.ProcessorBlueprint;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CreatureBlueprint extends VivariumObject
{
    // Physical traits
    @SerializedParameter
    private double _femaleProportion = 0.6;
    @SerializedParameter
    private int _maximumAge = 20000;
    @SerializedParameter
    private int _maximumGestation = 2000;
    @SerializedParameter
    private int _maximumFood = 2000;
    @SerializedParameter
    private int _maximumHealth = 2000;

    // Energy Uses
    @SerializedParameter
    private int _breedingFoodRate = -10;
    @SerializedParameter
    private int _eatingFoodRate = 500;
    @SerializedParameter
    private int _movingFoodRate = -1;
    @SerializedParameter
    private int _turningFoodRate = 0;
    @SerializedParameter
    private int _baseFoodRate = -1;
    @SerializedParameter
    private int _pregnantFoodRate = -1;

    // World Generation
    @SerializedParameter
    private double _initialGenerationProbability = 0.2;

    // Neurology
    @SerializedParameter
    private ProcessorBlueprint _processorBlueprint = null;

    private CreatureBlueprint()
    {
    }

    public double getFemaleThreshold()
    {
        return _femaleProportion;
    }

    public int getMaximumAge()
    {
        return _maximumAge;
    }

    public int getMaximumFood()
    {
        return _maximumFood;
    }

    public int getMaximumHealth()
    {
        return _maximumHealth;
    }

    public int getMaximumGestation()
    {
        return _maximumGestation;
    }

    public double getInitialGenerationProbability()
    {
        return this._initialGenerationProbability;
    }

    public void setInitialGenerationProbability(double probability)
    {
        this._initialGenerationProbability = probability;
    }

    public void setMaximumFood(int maximumFood)
    {
        this._maximumFood = maximumFood;
    }

    public int getBreedingFoodRate()
    {
        return _breedingFoodRate;
    }

    public void setBreedingFoodRate(int breedingFoodRate)
    {
        _breedingFoodRate = breedingFoodRate;
    }

    public int getEatingFoodRate()
    {
        return _eatingFoodRate;
    }

    public void setEatingFoodRate(int eatingFoodRate)
    {
        _eatingFoodRate = eatingFoodRate;
    }

    public int getMovingFoodRate()
    {
        return _movingFoodRate;
    }

    public void setMovingFoodRate(int movingFoodRate)
    {
        _movingFoodRate = movingFoodRate;
    }

    public int getBaseFoodRate()
    {
        return _baseFoodRate;
    }

    public void setBaseFoodRate(int baseFoodRate)
    {
        _baseFoodRate = baseFoodRate;
    }

    public int getPregnantFoodRate()
    {
        return _pregnantFoodRate;
    }

    public void setPregnantFoodRate(int pregnantFoodRate)
    {
        _pregnantFoodRate = pregnantFoodRate;
    }

    public static CreatureBlueprint makeDefault()
    {
        CreatureBlueprint s = new CreatureBlueprint();
        s.finalizeSerialization();
        s.setProcessorBlueprint(ProcessorBlueprint.makeDefault());
        return s;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    public static void main(String[] args)
    {
        System.out.println(CreatureBlueprint.makeDefault());
    }

    public ProcessorBlueprint getProcessorBlueprint()
    {
        return _processorBlueprint;
    }

    public void setProcessorBlueprint(ProcessorBlueprint processorBlueprint)
    {
        _processorBlueprint = processorBlueprint;
    }
}
