package io.vivarium.core.processor;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.johnuckele.vtest.Tester;

import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.test.FastTest;
import io.vivarium.test.IntegrationTest;

public class NormalizationTest
{
    @Test
    @Category({ FastTest.class, IntegrationTest.class })
    public void testBreedNormalize()
    {

        NeuralNetworkBlueprint processorBlueprint = NeuralNetworkBlueprint.makeDefault();
        processorBlueprint.setNormalizeAfterMutation(Math.sqrt(42));
        processorBlueprint.setRandomInitialization(true);
        ProcessorBlueprint[] processorBlueprints = new ProcessorBlueprint[] { processorBlueprint };

        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault();
        creatureBlueprint.setProcessorBlueprints(processorBlueprints);
        Creature c1 = new Creature(creatureBlueprint);
        Creature c2 = new Creature(creatureBlueprint);
        Creature c3 = new Creature(c1, c2);
        double childProcessorGenomeLength = 0;
        for (Processor p : c3.getProcessors())
        {
            if (p instanceof NeuralNetwork)
            {
                NeuralNetwork nn = (NeuralNetwork) p;
                childProcessorGenomeLength += nn.getGenomeLength();
            }
        }
        Tester.equal("Child processor genome should be 1", childProcessorGenomeLength, Math.sqrt(42), 0.000001);
    }
}
