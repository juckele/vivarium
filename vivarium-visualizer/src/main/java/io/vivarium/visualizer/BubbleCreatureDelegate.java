package io.vivarium.visualizer;

import io.vivarium.core.Action;
import io.vivarium.core.Creature;
import io.vivarium.core.bubble.BubblePosition;

public class BubbleCreatureDelegate
{
    private final Creature _creature;
    private final BubblePosition _position;

    private double _x1, _y1, _x2, _y2;
    private double _heading1, _heading2;
    private double _radius1, _radius2;
    private float _isPregnant1, _isPregnant2;
    private boolean _isSpawning;
    private boolean _isDying;

    public BubbleCreatureDelegate(Creature creature, BubblePosition position)
    {
        _creature = creature;
        _position = position;
        _x1 = _x2 = _position.getX();
        _y1 = _y2 = _position.getY();
        _heading1 = _heading2 = _position.getHeading();
        _radius1 = _radius2 = _position.getRadius();
        _isPregnant1 = _isPregnant2 = _creature.getGestation() > 0 ? 1 : 0;
        _isSpawning = _creature.getAction() == Action.SPAWN;
    }

    public void updateSnapshot()
    {
        _x1 = _x2;
        _x2 = _position.getX();

        _y1 = _y2;
        _y2 = _position.getY();

        _heading1 = _heading2;
        _heading2 = _position.getHeading();

        _radius1 = _radius2;
        _radius1 = _position.getRadius();

        _isPregnant1 = _isPregnant2;
        _isPregnant2 = _creature.getGestation() > 0 ? 1 : 0;
    }

    public boolean isDying()
    {
        return _isDying;
    }

    public void die()
    {
        _isDying = true;
    }

    public Creature getCreature()
    {
        return _creature;
    }

    public float getX(float interpolationFraction)
    {
        return (float) ((_x2 - _x1) * interpolationFraction + _x1);
    }

    public float getY(float interpolationFraction)
    {
        return (float) ((_y2 - _y1) * interpolationFraction + _y1);
    }

    public float getHeading(float interpolationFraction)
    {
        double heading1 = _heading1 * 180 / (Math.PI);
        double heading2 = _heading2 * 180 / (Math.PI);
        if ((heading1 == 0 && heading2 > 180) || (heading1 > 180 && heading2 == 0))
        {
            heading1 = heading1 == 0 ? 360 : heading1;
            heading2 = heading2 == 0 ? 360 : heading2;
        }
        double rotationInterpolated = (1 - interpolationFraction) * heading1 + interpolationFraction * heading2;
        return (float) rotationInterpolated;
    }

    public float getRadius(float interpolationFraction)
    {
        return (float) ((_radius2 - _radius1) * interpolationFraction + _radius1);
    }

    public float getPregnancy(float interpolationFraction)
    {
        return (_isPregnant2 - _isPregnant1) * interpolationFraction + _isPregnant1;
    }

    public float getScale(float interpolationFraction)
    {
        if (_isDying)
        {
            return 1 - interpolationFraction;
        }
        else if (_isSpawning)
        {
            return interpolationFraction;
        }
        else
        {
            return 1;
        }
    }

}
