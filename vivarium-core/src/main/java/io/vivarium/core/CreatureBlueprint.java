package io.vivarium.core;

import io.vivarium.core.processor.ProcessorBlueprint;
import io.vivarium.core.sensor.CompassRadar;
import io.vivarium.core.sensor.CreatureRadar;
import io.vivarium.core.sensor.EnergyRadar;
import io.vivarium.core.sensor.FoodRadar;
import io.vivarium.core.sensor.GenderRadar;
import io.vivarium.core.sensor.HealthRadar;
import io.vivarium.core.sensor.PathableRadar;
import io.vivarium.core.sensor.Sensor;
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
    @SerializedParameter
    private int _fightingDamageAmount = -50;
    @SerializedParameter
    private int _fightingFoodRate = -2;

    // World Generation
    @SerializedParameter
    private double _initialGenerationProbability = 0.2;

    // Neurology
    @SerializedParameter
    private ProcessorBlueprint _processorBlueprint = null;
    @SerializedParameter
    private int _sensorInputCount;
    @SerializedParameter
    private int _controllerOutputCount = 7;
    @SerializedParameter
    private int _memoryUnitCount = 0;
    @SerializedParameter
    private int _soundChannelCount = 0;
    @SerializedParameter
    private int _signChannelCount = 0;

    // Sensors
    @SerializedParameter
    private Sensor[] _sensors;

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

    public int getFightingDamageAmount()
    {
        return _fightingDamageAmount;
    }

    public int getFightingFoodRate()
    {
        return _fightingFoodRate;
    }

    public static CreatureBlueprint makeDefault()
    {
        CreatureBlueprint s = new CreatureBlueprint();
        s.finalizeSerialization();
        s.setProcessorBlueprint(ProcessorBlueprint.makeDefault());
        s._sensors = new Sensor[2];// [6];
        s._sensors[0] = new GenderRadar(0, 0, 0, 0);
        s._sensors[1] = new FoodRadar(0, 0, 0, 0);

        s._sensors[2] = new CreatureRadar(1, 1, 0, 0);
        s._sensors[3] = new PathableRadar(1, 1, 0, 0);
        s._sensors[4] = new EnergyRadar();
        s._sensors[5] = new HealthRadar();
        s._sensors[6] = new CompassRadar();
        s._sensorInputCount = 8; // 6;
        return s;
    }

    public static CreatureBlueprint makeWithSensors(Sensor[] sensors)
    {
        CreatureBlueprint s = new CreatureBlueprint();
        s.finalizeSerialization();
        s.setProcessorBlueprint(ProcessorBlueprint.makeDefault());
        s._sensors = sensors;
        int sensorInputCount = 0;
        for (Sensor sensor : sensors)
        {
            sensorInputCount += sensor.getSensorInputCount();
        }
        s._sensorInputCount = sensorInputCount;
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

    public int getHardProcessorInputs()
    {
        return _sensorInputCount;
    }

    public int getHardProcessorOutputs()
    {
        return _controllerOutputCount;
    }

    public int getMemoryUnitCount()
    {
        return this._memoryUnitCount;
    }

    public int getSoundChannelCount()
    {
        return this._soundChannelCount;
    }

    public int getSignChannelCount()
    {
        return this._signChannelCount;
    }

    public void setCreatureMemoryUnitCount(int memoryUnitCount)
    {
        this._memoryUnitCount = memoryUnitCount;
    }

    public void setCreatureSoundChannelCount(int soundChannelCount)
    {
        this._soundChannelCount = soundChannelCount;
    }

    public void setCreatureSignChannelCount(int signChannelCount)
    {
        this._signChannelCount = signChannelCount;
    }

    public int getTotalProcessorInputCount()
    {
        return this._sensorInputCount + this._memoryUnitCount + this._soundChannelCount + this._signChannelCount;
    }

    public int getTotalProcessorOutputCount()
    {
        return this._controllerOutputCount + this._memoryUnitCount + this._soundChannelCount + this._signChannelCount;
    }

    public Sensor[] getSensors()
    {
        return _sensors;
    }
}
