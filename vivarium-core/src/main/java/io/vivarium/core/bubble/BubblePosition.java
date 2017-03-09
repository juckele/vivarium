package io.vivarium.core.bubble;

import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class BubblePosition extends VivariumObject
{
    static
    {
        ClassRegistry.getInstance().register(BubblePosition.class);
    }

    @SerializedParameter
    private double _x = 0;
    @SerializedParameter
    private double _y = 0;
    @SerializedParameter
    private double _heading = 0;
    @SerializedParameter
    private double _radius = 1;

    // Private constructor for deserialization
    @SuppressWarnings("unused")
    private BubblePosition()
    {
    }

    public BubblePosition(double minX, double maxX, double minY, double maxY, double minHeading, double maxHeading,
            double minRadius, double maxRadius)
    {
        _x = (Rand.getInstance().getRandomPositiveDouble()) * (maxX - minX) + minX;
        _y = (Rand.getInstance().getRandomPositiveDouble()) * (maxY - minY) + minY;
        _heading = (Rand.getInstance().getRandomPositiveDouble()) * (maxHeading - minHeading) + minHeading;
        _radius = (Rand.getInstance().getRandomPositiveDouble()) * (maxRadius - minRadius) + minRadius;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    public double getHeading()
    {
        return _heading;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

    public double getRadius()
    {
        return _radius;
    }

    public void setHeading(double heading)
    {
        if (heading > 2 * Math.PI)
        {
            _heading = heading - 2 * Math.PI;
        }
        else if (heading < 0)
        {
            _heading = heading + 2 * Math.PI;
        }
        else
        {
            _heading = heading;
        }
    }

}
