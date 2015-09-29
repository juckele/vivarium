package io.vivarium.ga;

import io.vivarium.core.Creature;

public abstract class BehaviorBasedFitnessFunction extends FitnessFunction
{
    @Override
    public abstract double evaluate(Creature c);
}
