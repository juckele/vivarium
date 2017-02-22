package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.ClassRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class EnergySensor extends Sensor
{
    static
    {
        ClassRegistry.getInstance().register(EnergySensor.class);
    }

    public EnergySensor()
    {
        super(1);
    }

    @Override
    public void performSensing(GridWorld w, double[] inputs, int index, int r, int c, Creature creature)
    {
        inputs[index] = ((double) creature.getFood()) / creature.getCreatureBlueprint().getMaximumFood();
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
