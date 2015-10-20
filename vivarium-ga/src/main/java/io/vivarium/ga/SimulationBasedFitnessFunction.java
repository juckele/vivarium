/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.ga;

import io.vivarium.core.Creature;

public abstract class SimulationBasedFitnessFunction extends FitnessFunction
{

    @Override
    public abstract double evaluate(Creature c);
}
