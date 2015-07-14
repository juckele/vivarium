package com.johnuckele.vivarium.core;

public class Species
{
    // Creature Constants
    private static final double DEFAULT_FEMALE_THRESHOLD                        = 0.6;
    private static final int    MAXIMUM_AGE                                     = 20000;
    private static final int    MAXIMUM_GESTATION                               = 2000;
    private static final int    MAXIMUM_FOOD                                    = 2000;

    private static final int    BRAIN_INPUTS                                    = 5;
    private static final int    BRAIN_OUTPUTS                                   = 6;

    // Per species settings
    private static final double DEFAULT_INITIAL_CREATURE_GENERATION_PROBABILITY = 0.2;
    private static final int    DEFAULT_CREATURE_MEMORY_UNIT_COUNT              = 0;
    private static final int    DEFAULT_CREATURE_SOUND_CHANNEL_COUNT            = 0;

    // Per species - Model settings
    private static final double DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE           = 0.8;
    private static final double DEFAULT_MUTATION_RATE_EXPONENT                  = -7;
    private static final double DEFAULT_MUTATION_SMALL_SCALE_RATE               = 0.5;
    private static final double DEFAULT_MUTATION_RANDOM_RATE                    = 0.25;
    private static final double DEFAULT_MUTATION_FLIP_RATE                      = 0.25;

    private double              _femaleThreshold                                = DEFAULT_FEMALE_THRESHOLD;
    private int                 _maximumAge                                     = MAXIMUM_AGE;
    private int                 _maximumGestation                               = MAXIMUM_GESTATION;
    private int                 _maximumFood                                    = MAXIMUM_FOOD;

    private int                 _brainInputs                                    = BRAIN_INPUTS;
    private int                 _brainOutputs                                   = BRAIN_OUTPUTS;

    // Creaturology
    private int                 _memoryUnitCount                                = DEFAULT_CREATURE_MEMORY_UNIT_COUNT;
    private int                 _soundChannelCount                              = DEFAULT_CREATURE_SOUND_CHANNEL_COUNT;
    // Neurology
    private double              _inheritanceGaussianMixRate                     = DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE;
    private double              _mutationRateExponent                           = DEFAULT_MUTATION_RATE_EXPONENT;
    private double              _mutationRate                                   = Math.pow(2,
                                                                                        DEFAULT_MUTATION_RATE_EXPONENT);
    private double              _mutationSmallScaleRate                         = DEFAULT_MUTATION_SMALL_SCALE_RATE;
    private double              _mutationRandomRate                             = DEFAULT_MUTATION_RANDOM_RATE;
    private double              _mutationFlipRate                               = DEFAULT_MUTATION_FLIP_RATE;
    private double              _initialGenerationProbability                   = DEFAULT_INITIAL_CREATURE_GENERATION_PROBABILITY;

    // private Action[] _involuntaryActions;
    // private Action[] _voluntaryActions;

    public Species()
    {

    }

    public int getBrainInputs()
    {
        return _brainInputs;
    }

    public int getBrainOutputs()
    {
        return _brainOutputs;
    }

    public double getFemaleThreshold()
    {
        return _femaleThreshold;
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
        return this._mutationSmallScaleRate;
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
        return this._mutationFlipRate;
    }

    public double getMutationRandomRate()
    {
        return this._mutationRandomRate;
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
}
