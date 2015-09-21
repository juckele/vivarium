package com.johnuckele.vivarium.core;

import java.util.Map;

import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class Species implements MapSerializer
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
    private BrainType _brainType = BrainType.NEURAL_NETWORK;
    @SerializedParameter
    private int _hardBrainInputs = 5;
    @SerializedParameter
    private int _hardBrainOutputs = 6;
    @SerializedParameter
    private int _memoryUnitCount = 0;
    @SerializedParameter
    private int _soundChannelCount = 0;
    @SerializedParameter
    private boolean _randomInitialization = false;

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

    private Species()
    {
    }

    public int getHardBrainInputs()
    {
        return _hardBrainInputs;
    }

    public int getHardBrainOutputs()
    {
        return _hardBrainOutputs;
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

    public int getMaximumGestation()
    {
        return _maximumGestation;
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

    public double getInitialGenerationProbability()
    {
        return this._initialGenerationProbability;
    }

    public int getMemoryUnitCount()
    {
        return this._memoryUnitCount;
    }

    public int getSoundChannelCount()
    {
        return this._soundChannelCount;
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

    public void setInitialGenerationProbability(double probability)
    {
        this._initialGenerationProbability = probability;
    }

    public BrainType getBrainType()
    {
        return this._brainType;
    }

    public void setBrainType(BrainType type)
    {
        this._brainType = type;
    }

    public int getTotalBrainInputCount()
    {
        return this._hardBrainInputs + this._memoryUnitCount + this._soundChannelCount;
    }

    public int getTotalBrainOutputCount()
    {
        return this._hardBrainOutputs + this._memoryUnitCount + this._soundChannelCount;
    }

    public int getHiddenLayerCount()
    {
        return 0;
    }

    public void setRandomInitialization(boolean b)
    {
        this._randomInitialization = b;
    }

    public boolean getRandomInitialization()
    {
        return this._randomInitialization;
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

    public static void main(String[] args)
    {
        Species s = new Species();
        SerializationEngine engine = new SerializationEngine();
        SerializedCollection collection = engine.serialize(s);
        System.out.println(collection);
        System.out.println(s.getMutationRate());
    }

    public static Species makeUninitialized()
    {
        Species s = new Species();
        return s;
    }

    public static Species makeDefault()
    {
        Species s = new Species();
        new SerializationEngine().deserialize(s, SerializationEngine.EMPTY_OBJECT_MAP);
        return s;
    }

    public static Species makeFromMap(Map<String, Object> speciesValues)
    {
        Species s = new Species();
        new SerializationEngine().deserialize(s, speciesValues);
        return s;
    }

    public static Species makeCopy(Species original)
    {
        Species s = (Species) new SerializationEngine().makeCopy(original);
        return s;
    }

    @Override
    public void finalizeSerialization()
    {
        // update mutation rate
        _mutationRate = Math.pow(2, _mutationRateExponent);
    }
}
