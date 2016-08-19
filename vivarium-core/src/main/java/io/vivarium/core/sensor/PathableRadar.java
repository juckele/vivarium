package io.vivarium.core.sensor;

import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class PathableRadar extends Radar
{
    private PathableRadar()
    {
        super(0, 0, 0, 0);
    }

    public PathableRadar(int zMin, int zMax, int xMin, int xMax)
    {
        super(zMin, zMax, xMin, xMax);
    }

    @Override
    protected double senseSquare(World w, int r, int c)
    {
        return w.squareIsPathable(r, c) ? 1 : 0;
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
