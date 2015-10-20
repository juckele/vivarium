/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.experiment;

import java.util.ArrayList;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Species;
import io.vivarium.scripts.CreateWorld;
import io.vivarium.scripts.RunSimulation;
import io.vivarium.serialization.FileIO;
import io.vivarium.serialization.Format;

public class NormalizationConvergence
{
    /**
     * Runs the NormalizationConvergence experiment.
     *
     * Hypothesis: Normalizing creature genomes will cause speedier convergence and produce creatures with higher
     * average fitness by reducing genetic drift in 'inactive' genes. Genetic drift in inactive genes is hypothesized to
     * be dangerous because offspring have the risk of inheriting an active version when recombined from parents with
     * sufficiently distant inactive genes (this is a product of the Gaussian recombination of scalar genes used in
     * offspring creation)
     *
     * @param args
     */
    public static void main(String[] args)
    {
        // Make the blueprint with the default behavior
        Blueprint defaultBlueprint = Blueprint.makeDefault();
        defaultBlueprint.setSize(50);
        ArrayList<Species> defaultSpeciesList = new ArrayList<Species>();
        Species defaultSpecies = Species.makeDefault();
        defaultSpeciesList.add(defaultSpecies);
        defaultBlueprint.setSpecies(defaultSpeciesList);

        // Save the default blueprint
        FileIO.saveSerializer(defaultBlueprint, "defaultBlueprint.viv", Format.JSON);

        // Make the blueprint with the normalizing behavior
        Blueprint normalizingBlueprint = Blueprint.makeDefault();
        normalizingBlueprint.setSize(50);
        ArrayList<Species> normalizingSpeciesList = new ArrayList<Species>();
        Species normalizingSpecies = Species.makeDefault();
        normalizingSpecies.setNormalizeAfterMutation(true);
        normalizingSpeciesList.add(normalizingSpecies);
        normalizingBlueprint.setSpecies(normalizingSpeciesList);

        // Save the normalizing blueprint
        FileIO.saveSerializer(normalizingBlueprint, "normalizingBlueprint.viv", Format.JSON);

        // Start 20 simulations!
        for (int i = 1; i <= 10; i++)
        {
            Thread t1 = new WorldRunnerThread("default", i, 20, 1000000);
            t1.start();
            Thread t2 = new WorldRunnerThread("normalizing", i, 20, 1000000);
            t2.start();
        }
    }

    private static class WorldRunnerThread extends Thread
    {
        private final String _prefix;
        private final int _iteration;
        private final int _steps;
        private final int _ticksPerStep;

        WorldRunnerThread(String prefix, int iteration, int steps, int ticksPerStep)
        {
            this._prefix = prefix;
            this._iteration = iteration;
            this._steps = steps;
            this._ticksPerStep = ticksPerStep;
        }

        @Override
        public void run()
        {
            // Create a world
            {
                String[] args = { "-b", _prefix + "Blueprint.viv", "-o",
                        _prefix + "World" + _iteration + "_step" + 0 + ".viv" };
                CreateWorld.main(args);
            }

            // Run the world $steps times
            for (int i = 0; i < _steps; i++)
            {
                // for $ticksPerStep each time
                String[] args = { "-i", _prefix + "World" + _iteration + "_step" + i + ".viv", "-o",
                        _prefix + "World" + _iteration + "_step" + (i + 1) + ".viv", "-t", "" + _ticksPerStep };
                RunSimulation.main(args);
            }
        }

    }
}
