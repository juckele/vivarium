package com.johnuckele.vivarium.core;

import java.io.Serializable;

import com.johnuckele.vivarium.core.brain.BrainType;

public class Species implements Cloneable, Serializable
{
    private static final long    serialVersionUID                                = 1L;

    // Creature Constants
    private static final double  DEFAULT_FEMALE_THRESHOLD                        = 0.6;
    private static final int     MAXIMUM_AGE                                     = 20000;
    private static final int     MAXIMUM_GESTATION                               = 2000;
    private static final int     MAXIMUM_FOOD                                    = 2000;

    private static final int     HARD_BRAIN_INPUTS                               = 5;
    private static final int     HARD_BRAIN_OUTPUTS                              = 6;

    // Per species settings
    private static final double  DEFAULT_INITIAL_CREATURE_GENERATION_PROBABILITY = 0.2;
    private static final boolean DEFAULT_RANDOM_INITIALIZATION                   = false;
    private static final int     DEFAULT_CREATURE_MEMORY_UNIT_COUNT              = 0;
    private static final int     DEFAULT_CREATURE_SOUND_CHANNEL_COUNT            = 0;

    // Per species - Model settings
    private static final double  DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE           = 0.8;
    private static final double  DEFAULT_MUTATION_RATE_EXPONENT                  = -7;
    private static final double  DEFAULT_MUTATION_SMALL_SCALE_RATE               = 0.5;
    private static final double  DEFAULT_MUTATION_RANDOM_RATE                    = 0.25;
    private static final double  DEFAULT_MUTATION_FLIP_RATE                      = 0.25;

    private static String[]      GLYPHS                                          = { "中", "马", "心" };

    private double               _femaleThreshold                                = DEFAULT_FEMALE_THRESHOLD;
    private int                  _maximumAge                                     = MAXIMUM_AGE;
    private int                  _maximumGestation                               = MAXIMUM_GESTATION;
    private int                  _maximumFood                                    = MAXIMUM_FOOD;

    // Creature brains
    private BrainType            _brainType                                      = BrainType.NEURALNETWORK;
    private int                  _hardBrainInputs                                = HARD_BRAIN_INPUTS;
    private int                  _hardBrainOutputs                               = HARD_BRAIN_OUTPUTS;
    private int                  _memoryUnitCount                                = DEFAULT_CREATURE_MEMORY_UNIT_COUNT;
    private int                  _soundChannelCount                              = DEFAULT_CREATURE_SOUND_CHANNEL_COUNT;

    // Mutation
    private double               _inheritanceGaussianMixRate                     = DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE;
    private boolean              _randomInitialization                           = DEFAULT_RANDOM_INITIALIZATION;
    private boolean              _mutationEnabled;
    private double               _mutationRateExponent                           = DEFAULT_MUTATION_RATE_EXPONENT;
    private double               _mutationRate                                   = Math.pow(2,
                                                                                         DEFAULT_MUTATION_RATE_EXPONENT);
    private double               _mutationSmallScaleRate                         = DEFAULT_MUTATION_SMALL_SCALE_RATE;
    private double               _mutationRandomRate                             = DEFAULT_MUTATION_RANDOM_RATE;
    private double               _mutationFlipRate                               = DEFAULT_MUTATION_FLIP_RATE;
    private double               _initialGenerationProbability                   = DEFAULT_INITIAL_CREATURE_GENERATION_PROBABILITY;

    // private Action[] _involuntaryActions;
    // private Action[] _voluntaryActions;

    private int                  _speciesID;
    private String               _glyph                                          = "U";

    public Species(int speciesID)
    {
        _speciesID = speciesID;
        _glyph = GLYPHS[_speciesID];
    }

    public Object getGlyph()
    {
        return this._glyph;
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

    @Override
    public Species clone()
    {
        try
        {
            return (Species) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setMaximumFood(int maximumFood)
    {
        this._maximumFood = maximumFood;
    }
}
