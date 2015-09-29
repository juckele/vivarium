package io.vivarium.ga;

import io.vivarium.core.Creature;

public abstract class FitnessFunction
{
    public abstract double evaluate(Creature c);
}
