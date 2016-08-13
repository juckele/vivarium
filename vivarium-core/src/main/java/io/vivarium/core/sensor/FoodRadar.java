package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.Direction;
import io.vivarium.core.ItemType;
import io.vivarium.core.World;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class FoodRadar extends Sensor
{
    @SerializedParameter
    private int _zMin;
    @SerializedParameter
    private int _zMax;
    @SerializedParameter
    private int _xMin;
    @SerializedParameter
    private int _xMax;

    public FoodRadar(int zMin, int zMax, int xMin, int xMax)
    {
        super((zMax - zMin + 1) * (xMax - xMin + 1));
        _zMin = zMin;
        _zMax = zMax;
        _xMin = xMin;
        _xMax = xMax;
    }

    @Override
    public void performSensing(World w, double[] inputs, int index, int r, int c, Creature creature)
    {
        for (int z = _zMin; z <= _zMax; z++)
        {
            for (int x = _xMin; x <= _xMax; x++)
            {
                Direction sensorDirection = creature.getFacing();
                Direction orthaganalDirection = Direction.stepClockwise(sensorDirection);
                int targetR = r + Direction.getVerticalComponent(sensorDirection) * z
                        + Direction.getVerticalComponent(orthaganalDirection) * x;
                int targetC = c + Direction.getHorizontalComponent(sensorDirection) * z
                        + +Direction.getHorizontalComponent(orthaganalDirection) * x;
                inputs[index++] = w.getItem(targetR, targetC) == ItemType.FOOD ? 1 : 0;
            }
        }
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
