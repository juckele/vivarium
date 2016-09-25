package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.World;
import io.vivarium.serialization.ClassRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class GestationSensor extends Sensor
{
    static
    {
        ClassRegistry.getInstance().register(GestationSensor.class);
    }

    public GestationSensor()
    {
        super(1);
    }

    @Override
    public void performSensing(World w, double[] inputs, int index, int r, int c, Creature creature)
    {
        inputs[index] = ((double) creature.getGestation()) / creature.getCreatureBlueprint().getMaximumGestation();
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
