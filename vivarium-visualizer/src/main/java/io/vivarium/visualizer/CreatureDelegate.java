package io.vivarium.visualizer;

import io.vivarium.core.Action;
import io.vivarium.core.Creature;
import io.vivarium.core.Direction;

public class CreatureDelegate
{
    private final Creature _creature;

    private int _r1, _c1, _r2, _c2;
    private Direction _facing1, _facing2;
    private float _isPregnant1, _isPregnant2;
    private boolean _isSpawning;
    private boolean _isDying;

    public CreatureDelegate(Creature creature, int r, int c)
    {
        _creature = creature;
        _r1 = _r2 = r;
        _c1 = _c2 = c;
        _facing1 = _facing2 = _creature.getFacing();
        _isPregnant1 = _isPregnant2 = _creature.getGestation() > 0 ? 1 : 0;
        _isSpawning = _creature.getAction() == Action.SPAWN;
    }

    public void updateSnapshot(int r, int c)
    {
        _r1 = _r2;
        _r2 = r;

        _c1 = _c2;
        _c2 = c;

        _facing1 = _facing2;
        _facing2 = _creature.getFacing();

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

    public float getC(float interpolationFraction)
    {
        return (_c2 - _c1) * interpolationFraction + _c1;
    }

    public float getR(float interpolationFraction)
    {
        return (_r2 - _r1) * interpolationFraction + _r1;
    }

    public float getRotation(float interpolationFraction)
    {
        float rotation1 = (float) (Direction.getRadiansFromNorth(_facing1) * 180 / (Math.PI));
        float rotation2 = (float) (Direction.getRadiansFromNorth(_facing2) * 180 / (Math.PI));
        if ((rotation1 == 0 && rotation2 > 180) || (rotation1 > 180 && rotation2 == 0))
        {
            rotation1 = rotation1 == 0 ? 360 : rotation1;
            rotation2 = rotation2 == 0 ? 360 : rotation2;
        }
        float rotationInterpolated = (1 - interpolationFraction) * rotation1 + interpolationFraction * rotation2;
        return rotationInterpolated;
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
