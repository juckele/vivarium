package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.Direction;
import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CompassRadar extends Sensor
{
    public CompassRadar()
    {
        super(2);
    }

    @Override
    public void performSensing(World w, double[] inputs, int index, int r, int c, Creature creature)
    {

        Direction sensorDirection = creature.getFacing();

        int vertical = (Direction.getVerticalComponent(sensorDirection) + 1) / 2;
        inputs[index] = vertical;
        int horizontal = (Direction.getHorizontalComponent(sensorDirection) + 1) / 2;
        inputs[index++] = horizontal;

    }

    @Override
    public void finalizeSerialization()
    {
    }
}
