package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class Sensor extends VivariumObject
{
    @SerializedParameter
    private int _sensorInputCount;

    protected Sensor()
    {
    }

    protected Sensor(int sensorInputCount)
    {
        _sensorInputCount = sensorInputCount;
    }

    /**
     * Runs the sensors code and writes the sensed values to the input array.
     *
     * @param w
     *            The world this sensor is in.
     * @param inputs
     *            The array to write sensed values to.
     * @param index
     *            The index which the sensor should start writing sensed values at.
     * @param r
     *            The r position of the sensor.
     * @param c
     *            The c position of the sensor.
     * @param creature
     *            The creature this sensor is on.
     * @return The number of values sensed and written to inputs.
     */
    public int sense(GridWorld w, double[] inputs, int index, int r, int c, Creature creature)
    {
        performSensing(w, inputs, index, r, c, creature);
        return _sensorInputCount;
    }

    protected abstract void performSensing(GridWorld w, double[] inputs, int index, int r, int c, Creature creature);

    /**
     * Returns number of sensor inputs that this sensor writes.
     *
     * @return
     */
    public int getSensorInputCount()
    {
        return _sensorInputCount;
    }
}
