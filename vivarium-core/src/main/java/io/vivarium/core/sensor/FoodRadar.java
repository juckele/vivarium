
package io.vivarium.core.sensor;

import io.vivarium.core.ItemType;
import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class FoodRadar extends Radar
{
    public FoodRadar(int zMin, int zMax, int xMin, int xMax)
    {
        super(zMin, zMax, xMin, xMax);
    }

    @Override
    protected double senseSquare(World w, int r, int c)
    {
        return w.getItem(r, c) == ItemType.FOOD ? 1 : 0;
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
