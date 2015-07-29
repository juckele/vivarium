package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.johnuckele.vivarium.core.brain.BrainType;
import com.johnuckele.vivarium.serialization.DoubleParameter;
import com.johnuckele.vivarium.serialization.IntegerParameter;

public class Species implements Serializable
{
    private static final long      serialVersionUID               = 3L;

    // Energy Uses
    private static final int       EATING_FOOD_RATE               = 500;
    private static final int       MOVING_FOOD_RATE               = -1;
    private static final int       BASE_FOOD_RATE                 = -1;
    private static final int       PREGNANT_FOOD_RATE             = -2;

    // World Generation
    private static final double    INITIAL_GENERATION_PROBABILITY = 0.2;

    // Neurology
    private static final BrainType BRAIN_TYPE                     = BrainType.NEURALNETWORK;
    private static final int       HARD_BRAIN_INPUTS              = 5;
    private static final int       HARD_BRAIN_OUTPUTS             = 6;
    private static final boolean   RANDOM_INITIALIZATION          = false;
    private static final int       MEMORY_UNIT_COUNT              = 0;
    private static final int       SOUND_CHANNEL_COUNT            = 0;

    // Mutation
    private static final boolean   MUTATION_ENABLED               = true;
    private static final double    INHERITANCE_GAUSSIAN_MIX_RATE  = 0.8;
    private static final double    MUTATION_RATE_EXPONENT         = -7;
    private static final double    MUTATION_TYPE_SMALL_SCALE_RATE = 0.5;
    private static final double    MUTATION_TYPE_RANDOM_RATE      = 0.25;
    private static final double    MUTATION_TYPE_FLIP_RATE        = 0.25;

    // Physical traits
    @DoubleParameter(required = false, defaultValue = 0.6)
    private double                 _femaleProportion;

    @IntegerParameter(required = false, defaultValue = 20000)
    private int                    _maximumAge;

    @IntegerParameter(required = false, defaultValue = 2000)
    private int                    _maximumGestation;

    @IntegerParameter(required = false, defaultValue = 2000)
    private int                    _maximumFood;

    // Energy Uses
    @IntegerParameter(required = false, defaultValue = -10)
    private int                    _breedingFoodRate;
    private int                    _eatingFoodRate                = EATING_FOOD_RATE;
    private int                    _movingFoodRate                = MOVING_FOOD_RATE;
    private int                    _baseFoodRate                  = BASE_FOOD_RATE;
    private int                    _pregnantFoodRate              = PREGNANT_FOOD_RATE;

    // World Generation
    private double                 _initialGenerationProbability  = INITIAL_GENERATION_PROBABILITY;

    // Neurology
    private BrainType              _brainType                     = BRAIN_TYPE;
    private int                    _hardBrainInputs               = HARD_BRAIN_INPUTS;
    private int                    _hardBrainOutputs              = HARD_BRAIN_OUTPUTS;
    private int                    _memoryUnitCount               = MEMORY_UNIT_COUNT;
    private int                    _soundChannelCount             = SOUND_CHANNEL_COUNT;
    private boolean                _randomInitialization          = RANDOM_INITIALIZATION;

    // Mutation
    private boolean                _mutationEnabled               = MUTATION_ENABLED;
    private double                 _inheritanceGaussianMixRate    = INHERITANCE_GAUSSIAN_MIX_RATE;
    private double                 _mutationRateExponent          = MUTATION_RATE_EXPONENT;
    private double                 _mutationRate                  = Math.pow(2, MUTATION_RATE_EXPONENT);
    private double                 _mutationTypeSmallScaleRate    = MUTATION_TYPE_SMALL_SCALE_RATE;
    private double                 _mutationTypeRandomRate        = MUTATION_TYPE_RANDOM_RATE;
    private double                 _mutationTypeFlipRate          = MUTATION_TYPE_FLIP_RATE;

    // private Action[] _involuntaryActions;
    // private Action[] _voluntaryActions;

    public Species()
    {
        try
        {
            for (Field f : Species.class.getDeclaredFields())
            {
                DoubleParameter doubleParameter = f.getAnnotation(DoubleParameter.class);
                if (doubleParameter != null)
                {
                    f.setDouble(this, doubleParameter.defaultValue());
                }
                IntegerParameter intParameter = f.getAnnotation(IntegerParameter.class);
                if (intParameter != null)
                {
                    f.setInt(this, intParameter.defaultValue());
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public Species(Species s)
    {
        this._femaleProportion = s._femaleProportion;
        this._maximumAge = s._maximumAge;
        this._maximumGestation = s._maximumGestation;
        this._maximumFood = s._maximumFood;
        this._brainType = s._brainType;
        this._hardBrainInputs = s._hardBrainInputs;
        this._hardBrainOutputs = s._hardBrainOutputs;
        this._memoryUnitCount = s._memoryUnitCount;
        this._soundChannelCount = s._soundChannelCount;
        this._inheritanceGaussianMixRate = s._inheritanceGaussianMixRate;
        this._randomInitialization = s._randomInitialization;
        this._mutationEnabled = s._mutationEnabled;
        this._mutationRateExponent = s._mutationRateExponent;
        this._mutationRate = s._mutationRate;
        this._mutationTypeSmallScaleRate = s._mutationTypeSmallScaleRate;
        this._mutationTypeRandomRate = s._mutationTypeRandomRate;
        this._mutationTypeFlipRate = s._mutationTypeFlipRate;
        this._initialGenerationProbability = s._initialGenerationProbability;
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
        if (this._mutationEnabled)
        {
            return this._mutationRateExponent;
        }
        else
        {
            return Double.NaN;
        }
    }

    public double getMutationRate()
    {
        if (this._mutationEnabled)
        {
            return this._mutationRate;
        }
        else
        {
            return 0;
        }
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

    public void setMutationEnabled(boolean b)
    {
        this._mutationEnabled = false;
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

    public HashMap<String, String> serialize()
    {
        HashMap<String, String> map = new HashMap<String, String>();
        return map;
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
        System.out.println(s._femaleProportion);
    }
}
