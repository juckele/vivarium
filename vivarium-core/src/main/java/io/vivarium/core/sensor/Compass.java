package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.Direction;
import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Compass extends Sensor
{
    public Compass()
    {
        super(2);
    }

    @Override
    public void performSensing(World w, double[] inputs, int index, int r, int c, Creature creature)
    {
        Direction sensorDirection = creature.getFacing();

        inputs[index++] = Direction.getVerticalComponent(sensorDirection) / 2.0 + 0.5;
        inputs[index++] = Direction.getHorizontalComponent(sensorDirection) / 2.0 + 0.5;
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
