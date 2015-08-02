package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class Species implements MapSerializer, Serializable
{
    private static final long serialVersionUID = 4L;

    // Physical traits
    private double _femaleProportion;
    private int    _maximumAge;
    private int    _maximumGestation;
    private int    _maximumFood;

    // Energy Uses
    private int _breedingFoodRate;
    private int _eatingFoodRate;
    private int _movingFoodRate;
    private int _turningFoodRate;
    private int _baseFoodRate;
    private int _pregnantFoodRate;

    // World Generation
    private double _initialGenerationProbability;

    // Neurology
    private BrainType _brainType;
    private int       _hardBrainInputs;
    private int       _hardBrainOutputs;
    private int       _memoryUnitCount;
    private int       _soundChannelCount;
    private boolean   _randomInitialization;

    // Mutation
    private double _inheritanceGaussianMixRate;
    private double _mutationRateExponent;
    private double _mutationRate;
    private double _mutationTypeSmallScaleRate;
    private double _mutationTypeRandomRate;
    private double _mutationTypeFlipRate;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("femaleProportion", 0.6));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumAge", 20000));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumGestation", 2000));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumFood", 2000));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("breedingFoodRate", -10));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("eatingFoodRate", 500));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("movingFoodRate", -2));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("turningFoodRate", -1));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("baseFoodRate", -1));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("pregnantFoodRate", -1));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("initialGenerationProbability", 0.2));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("brainType", BrainType.NEURAL_NETWORK));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("hardBrainInputs", 5));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("hardBrainOutputs", 6));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("memoryUnitCount", 0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("soundChannelCount", 0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("randomInitialization", false));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("inheritanceGaussianMixRate", 0.8));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationRateExponent", -7.0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationTypeSmallScaleRate", 0.5));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationTypeRandomRate", 0.25));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationTypeFlipRate", 0.25));
    }

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
        SerializationEngine engine = new SerializationEngine();
        HashMap<String, String> speciesMap = new HashMap<String, String>();
        speciesMap.put("+type", "Species");
        speciesMap.put("+id", "0");
        speciesMap.put("initialGenerationProbability", "0.5");
        Species s = (Species) engine.deserialize(speciesMap);
        // Species s = new Species();
        System.out.println(s._femaleProportion);
        SerializedCollection collection = engine.serialize(s);
        System.out.println(collection);
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
        // SerializationEngine se = new SerializationEngine();
        // collection = se.serialize(original);
        // Species copy = (Species) se.deserialize(map);
        return null;
    }

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.SPECIES;
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return Species.SERIALIZED_PARAMETERS;
    }

    @Override
    public String getValue(String key)
    {
        switch (key)
        {
            case "femaleProportion":
                return "" + this._femaleProportion;
            case "maximumAge":
                return "" + this._maximumAge;
            case "maximumGestation":
                return "" + this._maximumGestation;
            case "maximumFood":
                return "" + this._maximumFood;
            case "breedingFoodRate":
                return "" + this._breedingFoodRate;
            case "eatingFoodRate":
                return "" + this._eatingFoodRate;
            case "movingFoodRate":
                return "" + this._movingFoodRate;
            case "turningFoodRate":
                return "" + this._turningFoodRate;
            case "baseFoodRate":
                return "" + this._baseFoodRate;
            case "pregnantFoodRate":
                return "" + this._pregnantFoodRate;
            case "initialGenerationProbability":
                return "" + this._initialGenerationProbability;
            case "brainType":
                return this._brainType.name();
            case "hardBrainInputs":
                return "" + this._hardBrainInputs;
            case "hardBrainOutputs":
                return "" + this._hardBrainOutputs;
            case "memoryUnitCount":
                return "" + this._memoryUnitCount;
            case "soundChannelCount":
                return "" + this._soundChannelCount;
            case "randomInitialization":
                return "" + this._randomInitialization;
            case "inheritanceGaussianMixRate":
                return "" + this._inheritanceGaussianMixRate;
            case "mutationRateExponent":
                return "" + this._mutationRateExponent;
            case "mutationTypeSmallScaleRate":
                return "" + this._mutationTypeSmallScaleRate;
            case "mutationTypeRandomRate":
                return "" + this._mutationTypeRandomRate;
            case "mutationTypeFlipRate":
                return "" + this._mutationTypeFlipRate;
            default:
                throw new IllegalArgumentException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, String value)
    {
        switch (key)
        {
            case "femaleProportion":
                this._femaleProportion = Double.parseDouble(value);
                break;
            case "maximumAge":
                this._maximumAge = Integer.parseInt(value);
                break;
            case "maximumGestation":
                this._maximumGestation = Integer.parseInt(value);
                break;
            case "maximumFood":
                this._maximumFood = Integer.parseInt(value);
                break;
            case "breedingFoodRate":
                this._breedingFoodRate = Integer.parseInt(value);
                break;
            case "eatingFoodRate":
                this._eatingFoodRate = Integer.parseInt(value);
                break;
            case "movingFoodRate":
                this._movingFoodRate = Integer.parseInt(value);
                break;
            case "turningFoodRate":
                this._turningFoodRate = Integer.parseInt(value);
                break;
            case "baseFoodRate":
                this._baseFoodRate = Integer.parseInt(value);
                break;
            case "pregnantFoodRate":
                this._pregnantFoodRate = Integer.parseInt(value);
                break;
            case "initialGenerationProbability":
                this._initialGenerationProbability = Double.parseDouble(value);
                break;
            case "brainType":
                this._brainType = BrainType.valueOf(value);
                break;
            case "hardBrainInputs":
                this._hardBrainInputs = Integer.parseInt(value);
                break;
            case "hardBrainOutputs":
                this._hardBrainOutputs = Integer.parseInt(value);
                break;
            case "memoryUnitCount":
                this._memoryUnitCount = Integer.parseInt(value);
                break;
            case "soundChannelCount":
                this._soundChannelCount = Integer.parseInt(value);
                break;
            case "randomInitialization":
                this._randomInitialization = Boolean.parseBoolean(value);
                break;
            case "inheritanceGaussianMixRate":
                this._inheritanceGaussianMixRate = Double.parseDouble(value);
                break;
            case "mutationRateExponent":
                this._mutationRateExponent = Double.parseDouble(value);
                break;
            case "mutationTypeSmallScaleRate":
                this._mutationTypeSmallScaleRate = Double.parseDouble(value);
                break;
            case "mutationTypeRandomRate":
                this._mutationTypeRandomRate = Double.parseDouble(value);
                break;
            case "mutationTypeFlipRate":
                this._mutationTypeFlipRate = Double.parseDouble(value);
                break;
            default:
                throw new IllegalArgumentException("Key " + key + " not in mapped parameters");
        }
    }
}
