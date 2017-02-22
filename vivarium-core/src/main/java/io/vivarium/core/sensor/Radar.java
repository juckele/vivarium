package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.Direction;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class Radar extends Sensor
{
    @SerializedParameter
    private int _zMin;
    @SerializedParameter
    private int _zMax;
    @SerializedParameter
    private int _xMin;
    @SerializedParameter
    private int _xMax;

    public Radar(int zMin, int zMax, int xMin, int xMax)
    {
        super((zMax - zMin + 1) * (xMax - xMin + 1));
        _zMin = zMin;
        _zMax = zMax;
        _xMin = xMin;
        _xMax = xMax;
    }

    @Override
    public void performSensing(GridWorld w, double[] inputs, int index, int r, int c, Creature creature)
    {
        // Determine direction of sensor
        Direction sensorDirection = creature.getFacing();
        Direction orthaganalDirection = Direction.stepClockwise(sensorDirection);

        // Loop over z and x dimensions of the sensor
        for (int z = _zMin; z <= _zMax; z++)
        {
            for (int x = _xMin; x <= _xMax; x++)
            {
                // Translate x and z values with directions into r and c values
                int targetR = r + Direction.getVerticalComponent(sensorDirection) * z
                        + Direction.getVerticalComponent(orthaganalDirection) * x;
                int targetC = c + Direction.getHorizontalComponent(sensorDirection) * z
                        + +Direction.getHorizontalComponent(orthaganalDirection) * x;

                // If r and c are within bounds of the world, detect the square
                if (targetR > -1 && targetC > -1 && targetR < w.getHeight() && targetC < w.getWidth())
                {
                    inputs[index++] = senseSquare(w, targetR, targetC);
                }
                else
                {
                    // When not in bounds, radars always read 0
                    inputs[index++] = 0;
                }
            }
        }
    }

    abstract protected double senseSquare(GridWorld w, int r, int c);
}
