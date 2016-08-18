package io.vivarium.core.sensor;

import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CreatureRadar extends Radar
{
    public CreatureRadar(int zMin, int zMax, int xMin, int xMax)
    {
        super(zMin, zMax, xMin, xMax);
    }

    @Override
    protected double senseSquare(World w, int r, int c)
    {
        return w.getCreature(r, c) != null ? 1 : 0;
    }

    @Override
    public void finalizeSerialization()
    {
    }
}