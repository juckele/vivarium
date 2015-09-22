package com.johnuckele.vivarium.ga;

import com.johnuckele.vivarium.core.Creature;

public abstract class SimulationBasedFitnessFunction extends FitnessFunction
{

    @Override
    public abstract double evaluate(Creature c);
}
