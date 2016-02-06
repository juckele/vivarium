/*
 * Copyright © 2016 John H Uckele. All rights reserved.
 */

package io.vivarium.core.processor;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.Creature;
import io.vivarium.core.Species;

public class NormalizationTest
{
    @Test
    public void testBreedNormalize()
    {
        Species species = Species.makeDefault();
        species.setNormalizeAfterMutation(Math.sqrt(42));
        species.setRandomInitialization(true);
        Creature c1 = new Creature(species);
        Creature c2 = new Creature(species);
        Creature c3 = new Creature(c1, c2);
        double childProcessorGenomeLength = c3.getProcessor().getGenomeLength();
        Tester.equal("Child processor genome should be 1", childProcessorGenomeLength, Math.sqrt(42), 0.000001);
    }
}
