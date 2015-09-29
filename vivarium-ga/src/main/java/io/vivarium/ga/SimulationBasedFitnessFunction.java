package io.vivarium.ga;

import io.vivarium.core.Creature;

public abstract class SimulationBasedFitnessFunction extends FitnessFunction
{

    @Override
    public abstract double evaluate(Creature c);
}
