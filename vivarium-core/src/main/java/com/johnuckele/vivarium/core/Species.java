package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.annotations.BooleanParameter;
import com.johnuckele.vivarium.serialization.annotations.BrainTypeParameter;
import com.johnuckele.vivarium.serialization.annotations.DoubleParameter;
import com.johnuckele.vivarium.serialization.annotations.IntegerParameter;

public class Species implements MapSerializer, Serializable
{
    private static final long serialVersionUID = 4L;

    // Physical traits
    @DoubleParameter(defaultValue = 0.6)
    private double            _femaleProportion;

    @IntegerParameter(defaultValue = 20000)
    private int               _maximumAge;

    @IntegerParameter(defaultValue = 2000)
    private int               _maximumGestation;

    @IntegerParameter(defaultValue = 2000)
    private int               _maximumFood;

    // Energy Uses
    @IntegerParameter(defaultValue = -10)
    private int               _breedingFoodRate;

    @IntegerParameter(defaultValue = 500)
    private int               _eatingFoodRate;

    @IntegerParameter(defaultValue = -2)
    private int               _movingFoodRate;

    @IntegerParameter(defaultValue = -1)
    private int               _turningFoodRate;

    @IntegerParameter(defaultValue = -1)
    private int               _baseFoodRate;

    @IntegerParameter(defaultValue = -1)
    private int               _pregnantFoodRate;

    // World Generation
    @DoubleParameter(defaultValue = 0.2)
    private double            _initialGenerationProbability;

    // Neurology
    @BrainTypeParameter(defaultValue = BrainType.NEURAL_NETWORK)
    private BrainType         _brainType;
    @IntegerParameter(defaultValue = 5)
    private int               _hardBrainInputs;
    @IntegerParameter(defaultValue = 6)
    private int               _hardBrainOutputs;
    @IntegerParameter(defaultValue = 0)
    private int               _memoryUnitCount;
    @IntegerParameter(defaultValue = 0)
    private int               _soundChannelCount;
    @BooleanParameter(defaultValue = false)
    private boolean           _randomInitialization;

    // Mutation
    @DoubleParameter(defaultValue = 0.8)
    private double            _inheritanceGaussianMixRate;
    @DoubleParameter(defaultValue = -7)
    private double            _mutationRateExponent;
    private double            _mutationRate;
    @DoubleParameter(defaultValue = 0.5)
    private double            _mutationTypeSmallScaleRate;
    @DoubleParameter(defaultValue = 0.25)
    private double            _mutationTypeRandomRate;
    @DoubleParameter(defaultValue = 0.25)
    private double            _mutationTypeFlipRate;

    private void updateMutationRate()
    {
        _mutationRate = Math.pow(2, _mutationRateExponent);
    }

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
        SerializationEngine engine = new SerializationEngine(null);
        HashMap<String, String> speciesMap = new HashMap<String, String>();
        speciesMap.put("+type", "Species");
        speciesMap.put("+id", "0");
        speciesMap.put("initialGenerationProbability", "0.5");
        Species s = (Species) engine.deserialize(speciesMap);
        // Species s = new Species();
        System.out.println(s._femaleProportion);
        HashMap<String, String> map = engine.serialize(s);
        System.out.println(map);
        System.out.println(s.getMutationRate());
    }

    @Override
    public Set<MapSerializer> getReferences()
    {
        return new HashSet<MapSerializer>();
    }

    @Override
    public Map<String, String> finalizeSerialization(Map<String, String> map, Map<MapSerializer, Integer> referenceMap)
    {
        return map;
    }

    @Override
    public void finalizeDeserialization(Map<String, String> map, Map<Integer, MapSerializer> dereferenceMap)
    {
        updateMutationRate();
    }

    public static Species makeUninitialized()
    {
        Species s = new Species();
        s.updateMutationRate();
        return s;
    }

    public static Species makeDefault()
    {
        Species s = new Species();
        SerializationEngine.deserialize(s, SerializationEngine.EMPTY_OBJECT_MAP);
        return s;
    }

    public static Species makeCopy(Species original)
    {
        // TODO: Use serialization
        SerializationEngine se = new SerializationEngine(null);
        HashMap<String, String> map = se.serialize(original);
        Species copy = (Species) se.deserialize(map);
        return copy;
    }
}
