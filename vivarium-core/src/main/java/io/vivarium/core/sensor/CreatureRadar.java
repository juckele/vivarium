package io.vivarium.core.sensor;

import io.vivarium.core.GridWorld;
import io.vivarium.serialization.ClassRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CreatureRadar extends Radar
{
    static
    {
        ClassRegistry.getInstance().register(CreatureRadar.class);
    }

    private CreatureRadar()
    {
        super(0, 0, 0, 0);
    }

    public CreatureRadar(int zMin, int zMax, int xMin, int xMax)
    {
        super(zMin, zMax, xMin, xMax);
    }

    @Override
    protected double senseSquare(GridWorld w, int r, int c)
    {
        return w.getCreature(r, c) != null ? 1 : 0;
    }

    @Override
    public void finalizeSerialization()
    {
    }
}