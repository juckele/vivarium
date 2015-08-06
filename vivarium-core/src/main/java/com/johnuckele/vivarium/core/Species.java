package com.johnuckele.vivarium.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedCollection;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public class Species implements MapSerializer
{
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
        SERIALIZED_PARAMETERS.add(new SerializedParameter("femaleProportion", Double.class, 0.6));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumAge", Integer.class, 20000));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumGestation", Integer.class, 2000));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumFood", Integer.class, 2000));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("breedingFoodRate", Integer.class, -10));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("eatingFoodRate", Integer.class, 500));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("movingFoodRate", Integer.class, -2));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("turningFoodRate", Integer.class, 0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("baseFoodRate", Integer.class, -1));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("pregnantFoodRate", Integer.class, -1));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("initialGenerationProbability", Double.class, 0.2));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("brainType", BrainType.class, BrainType.NEURAL_NETWORK));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("hardBrainInputs", Integer.class, 5));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("hardBrainOutputs", Integer.class, 6));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("memoryUnitCount", Integer.class, 0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("soundChannelCount", Integer.class, 0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("randomInitialization", Boolean.class, false));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("inheritanceGaussianMixRate", Double.class, 0.8));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationRateExponent", Double.class, -7.0));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationTypeSmallScaleRate", Double.class, 0.5));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationTypeRandomRate", Double.class, 0.25));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("mutationTypeFlipRate", Double.class, 0.25));
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
        Species s = (Species) engine.deserializeMap(speciesMap);
        // Species s = new Species();
        System.out.println(s._femaleProportion);
        SerializedCollection collection = engine.serialize(s);
        System.out.println(collection);
        System.out.println(s.getMutationRate());
    }

    @Override
    public LinkedList<MapSerializer> getReferences()
    {
        return new LinkedList<MapSerializer>();
    }

    @Override
    public Map<String, String> finalizeSerialization(Map<String, String> map, Map<MapSerializer, Integer> referenceMap)
    {
        return map;
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
        new SerializationEngine().deserialize(s, SerializationEngine.EMPTY_OBJECT_MAP);
        return s;
    }

    public static Species makeCopy(Species original)
    {
        return (Species) new SerializationEngine().makeCopy(original);
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
    public Object getValue(String key)
    {
        switch (key)
        {
            case "femaleProportion":
                return this._femaleProportion;
            case "maximumAge":
                return this._maximumAge;
            case "maximumGestation":
                return this._maximumGestation;
            case "maximumFood":
                return this._maximumFood;
            case "breedingFoodRate":
                return this._breedingFoodRate;
            case "eatingFoodRate":
                return this._eatingFoodRate;
            case "movingFoodRate":
                return this._movingFoodRate;
            case "turningFoodRate":
                return this._turningFoodRate;
            case "baseFoodRate":
                return this._baseFoodRate;
            case "pregnantFoodRate":
                return this._pregnantFoodRate;
            case "initialGenerationProbability":
                return this._initialGenerationProbability;
            case "brainType":
                return this._brainType;
            case "hardBrainInputs":
                return this._hardBrainInputs;
            case "hardBrainOutputs":
                return this._hardBrainOutputs;
            case "memoryUnitCount":
                return this._memoryUnitCount;
            case "soundChannelCount":
                return this._soundChannelCount;
            case "randomInitialization":
                return this._randomInitialization;
            case "inheritanceGaussianMixRate":
                return this._inheritanceGaussianMixRate;
            case "mutationRateExponent":
                return this._mutationRateExponent;
            case "mutationTypeSmallScaleRate":
                return this._mutationTypeSmallScaleRate;
            case "mutationTypeRandomRate":
                return this._mutationTypeRandomRate;
            case "mutationTypeFlipRate":
                return this._mutationTypeFlipRate;
            default:
                throw new IllegalArgumentException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "femaleProportion":
                this._femaleProportion = (Double) value;
                break;
            case "maximumAge":
                this._maximumAge = (Integer) value;
                break;
            case "maximumGestation":
                this._maximumGestation = (Integer) value;
                break;
            case "maximumFood":
                this._maximumFood = (Integer) value;
                break;
            case "breedingFoodRate":
                this._breedingFoodRate = (Integer) value;
                break;
            case "eatingFoodRate":
                this._eatingFoodRate = (Integer) value;
                break;
            case "movingFoodRate":
                this._movingFoodRate = (Integer) value;
                break;
            case "turningFoodRate":
                this._turningFoodRate = (Integer) value;
                break;
            case "baseFoodRate":
                this._baseFoodRate = (Integer) value;
                break;
            case "pregnantFoodRate":
                this._pregnantFoodRate = (Integer) value;
                break;
            case "initialGenerationProbability":
                this._initialGenerationProbability = (Double) value;
                break;
            case "brainType":
                this._brainType = (BrainType) value;
                break;
            case "hardBrainInputs":
                this._hardBrainInputs = (Integer) value;
                break;
            case "hardBrainOutputs":
                this._hardBrainOutputs = (Integer) value;
                break;
            case "memoryUnitCount":
                this._memoryUnitCount = (Integer) value;
                break;
            case "soundChannelCount":
                this._soundChannelCount = (Integer) value;
                break;
            case "randomInitialization":
                this._randomInitialization = (Boolean) value;
                break;
            case "inheritanceGaussianMixRate":
                this._inheritanceGaussianMixRate = (Double) value;
                break;
            case "mutationRateExponent":
                this._mutationRateExponent = (Double) value;
                updateMutationRate();
                break;
            case "mutationTypeSmallScaleRate":
                this._mutationTypeSmallScaleRate = (Double) value;
                break;
            case "mutationTypeRandomRate":
                this._mutationTypeRandomRate = (Double) value;
                break;
            case "mutationTypeFlipRate":
                this._mutationTypeFlipRate = (Double) value;
                break;
            default:
                throw new IllegalArgumentException("Key " + key + " not in mapped parameters");
        }
    }
}
