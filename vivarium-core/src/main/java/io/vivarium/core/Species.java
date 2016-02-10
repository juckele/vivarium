/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core;

import io.vivarium.core.processor.ProcessorType;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Species extends VivariumObject
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
    private ProcessorType _processorType = ProcessorType.NEURAL_NETWORK;
    @SerializedParameter
    private int _hardProcessorInputs = 5;
    @SerializedParameter
    private int _hardProcessorOutputs = 6;
    @SerializedParameter
    private int _memoryUnitCount = 0;
    @SerializedParameter
    private int _soundChannelCount = 0;
    @SerializedParameter
    private boolean _randomInitialization = false;
    @SerializedParameter
    private double _normalizeAfterMutation = 0;

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

    public int getHardProcessorInputs()
    {
        return _hardProcessorInputs;
    }

    public int getHardProcessorOutputs()
    {
        return _hardProcessorOutputs;
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

    public double getNormalizeAfterMutation()
    {
        return this._normalizeAfterMutation;
    }

    public void setNormalizeAfterMutation(double normalizeAfterMutation)
    {
        _normalizeAfterMutation = normalizeAfterMutation;
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

    public ProcessorType getProcessorType()
    {
        return this._processorType;
    }

    public void setProcessorType(ProcessorType type)
    {
        this._processorType = type;
    }

    public int getTotalProcessorInputCount()
    {
        return this._hardProcessorInputs + this._memoryUnitCount + this._soundChannelCount;
    }

    public int getTotalProcessorOutputCount()
    {
        return this._hardProcessorOutputs + this._memoryUnitCount + this._soundChannelCount;
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

    public static Species makeDefault()
    {
        Species s = new Species();
        s.finalizeSerialization();
        return s;
    }

    @Override
    public void finalizeSerialization()
    {
        // update mutation rate
        _mutationRate = Math.pow(2, _mutationRateExponent);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + _baseFoodRate;
        result = prime * result + _breedingFoodRate;
        result = prime * result + _eatingFoodRate;
        long temp;
        temp = Double.doubleToLongBits(_femaleProportion);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + _hardProcessorInputs;
        result = prime * result + _hardProcessorOutputs;
        temp = Double.doubleToLongBits(_inheritanceGaussianMixRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_initialGenerationProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + _maximumAge;
        result = prime * result + _maximumFood;
        result = prime * result + _maximumGestation;
        result = prime * result + _memoryUnitCount;
        result = prime * result + _movingFoodRate;
        temp = Double.doubleToLongBits(_mutationRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_mutationRateExponent);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_mutationTypeFlipRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_mutationTypeRandomRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_mutationTypeSmallScaleRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_normalizeAfterMutation);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + _pregnantFoodRate;
        result = prime * result + ((_processorType == null) ? 0 : _processorType.hashCode());
        result = prime * result + (_randomInitialization ? 1231 : 1237);
        result = prime * result + _soundChannelCount;
        result = prime * result + _turningFoodRate;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Species other = (Species) obj;
        if (_baseFoodRate != other._baseFoodRate)
        {
            return false;
        }
        if (_breedingFoodRate != other._breedingFoodRate)
        {
            return false;
        }
        if (_eatingFoodRate != other._eatingFoodRate)
        {
            return false;
        }
        if (Double.doubleToLongBits(_femaleProportion) != Double.doubleToLongBits(other._femaleProportion))
        {
            return false;
        }
        if (_hardProcessorInputs != other._hardProcessorInputs)
        {
            return false;
        }
        if (_hardProcessorOutputs != other._hardProcessorOutputs)
        {
            return false;
        }
        if (Double.doubleToLongBits(_inheritanceGaussianMixRate) != Double
                .doubleToLongBits(other._inheritanceGaussianMixRate))
        {
            return false;
        }
        if (Double.doubleToLongBits(_initialGenerationProbability) != Double
                .doubleToLongBits(other._initialGenerationProbability))
        {
            return false;
        }
        if (_maximumAge != other._maximumAge)
        {
            return false;
        }
        if (_maximumFood != other._maximumFood)
        {
            return false;
        }
        if (_maximumGestation != other._maximumGestation)
        {
            return false;
        }
        if (_memoryUnitCount != other._memoryUnitCount)
        {
            return false;
        }
        if (_movingFoodRate != other._movingFoodRate)
        {
            return false;
        }
        if (Double.doubleToLongBits(_mutationRate) != Double.doubleToLongBits(other._mutationRate))
        {
            return false;
        }
        if (Double.doubleToLongBits(_mutationRateExponent) != Double.doubleToLongBits(other._mutationRateExponent))
        {
            return false;
        }
        if (Double.doubleToLongBits(_mutationTypeFlipRate) != Double.doubleToLongBits(other._mutationTypeFlipRate))
        {
            return false;
        }
        if (Double.doubleToLongBits(_mutationTypeRandomRate) != Double.doubleToLongBits(other._mutationTypeRandomRate))
        {
            return false;
        }
        if (Double.doubleToLongBits(_mutationTypeSmallScaleRate) != Double
                .doubleToLongBits(other._mutationTypeSmallScaleRate))
        {
            return false;
        }
        if (Double.doubleToLongBits(_normalizeAfterMutation) != Double.doubleToLongBits(other._normalizeAfterMutation))
        {
            return false;
        }
        if (_pregnantFoodRate != other._pregnantFoodRate)
        {
            return false;
        }
        if (_processorType != other._processorType)
        {
            return false;
        }
        if (_randomInitialization != other._randomInitialization)
        {
            return false;
        }
        if (_soundChannelCount != other._soundChannelCount)
        {
            return false;
        }
        if (_turningFoodRate != other._turningFoodRate)
        {
            return false;
        }
        return true;
    }
}
