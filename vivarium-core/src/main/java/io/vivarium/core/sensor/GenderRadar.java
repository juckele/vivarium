package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.World;
import io.vivarium.serialization.ClassRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class GenderRadar extends Radar
{
    static
    {
        ClassRegistry.getInstance().register(GenderRadar.class);
    }

    private GenderRadar()
    {
        super(0, 0, 0, 0);
    }

    public GenderRadar(int zMin, int zMax, int xMin, int xMax)
    {
        super(zMin, zMax, xMin, xMax);
    }

    @Override
    protected double senseSquare(World w, int r, int c)
    {
        Creature creature = w.getCreature(r, c);
        if (creature != null)
        {
            return creature.getIsFemale() ? 1 : 0;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void finalizeSerialization()
    {
    }
}