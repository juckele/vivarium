package io.vivarium.core.processor;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class NormalizationTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
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
