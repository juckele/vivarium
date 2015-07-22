package com.johnuckele.vivarium.ga;

import java.util.ArrayList;
import java.util.Collections;

import org.javatuples.Pair;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.WorldVariables;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

/**
 * A class for running a standard genetic algorithm across a population of creatures testing them with one or more
 * {@code FitnessFunction}.
 */
public class GeneticAlgorithmRunner
{
    Species                           _species;
    int                               _currentGeneration = 1;
    int                               _generations       = 100;
    int                               _populationSize    = 100;
    ArrayList<Pair<Double, Creature>> _population        = new ArrayList<Pair<Double, Creature>>(_populationSize);
    private FitnessFunction           _fitnessFunction;

    public GeneticAlgorithmRunner(Species species, FitnessFunction fitnessFunction)
    {
        this._fitnessFunction = fitnessFunction;

        this._species = species;

        buildInitialPopulation();
    }

    private void buildInitialPopulation()
    {
        WorldVariables wv = new WorldVariables(_species);
        for (int i = 0; i < _populationSize; i++)
        {
            Creature c = new Creature(_species, wv);
            System.out.println(c.getBrain().render(RenderCode.BRAIN_WEIGHTS));
            _population.add(new Pair<Double, Creature>(0.0, c));
        }
    }

    private void run()
    {
        while (_currentGeneration <= _generations)
        {
            evaluatePopulation();
            updatePopulation();
            // Finally finish and increment the generation counters
            _currentGeneration++;
        }
    }

    private void evaluatePopulation()
    {
        for (int i = 0; i < _population.size(); i++)
        {
            evaluatePopulationMember(i);
        }
    }

    private void evaluatePopulationMember(int i)
    {
        Pair<Double, Creature> populationMember = _population.get(i);
        Creature c = populationMember.getValue1();
        double fitness = _fitnessFunction.evaluate(c);
        Pair<Double, Creature> updatedPopulationMember = new Pair<Double, Creature>(fitness, c);
        _population.set(i, updatedPopulationMember);
    }

    private void updatePopulation()
    {

        Collections.sort(_population);
        System.out.println("Gen... " + this._currentGeneration);
        System.out.println("Worst member " + _population.get(0).getValue0());
        System.out.println("Median member " + _population.get(_population.size() / 2).getValue0());
        System.out.println("3rd Best member " + _population.get(_population.size() - 3).getValue0());
        System.out.println("2nd Best member " + _population.get(_population.size() - 2).getValue0());
        System.out.println("1st Best member " + _population.get(_population.size() - 1).getValue0());
        System.out.println("1st Best member\n"
                + _population.get(_population.size() - 1).getValue1().getBrain().render(RenderCode.BRAIN_WEIGHTS));
        ArrayList<Pair<Double, Creature>> newPopulation = new ArrayList<Pair<Double, Creature>>(_populationSize);
        double fitnessSum = 0;
        for (int i = 0; i < _populationSize; i++)
        {
            fitnessSum += _population.get(i).getValue0();
        }
        System.out.println("Total fitness is " + fitnessSum);
        for (int i = 0; i < _populationSize; i++)
        {
            int parent1Index = Math.max(Rand.getRandomInt(_populationSize - i) + i,
                    Rand.getRandomInt(_populationSize - i) + i);
            int parent2Index = Math.max(Rand.getRandomInt(_populationSize - i) + i,
                    Rand.getRandomInt(_populationSize - i) + i);
            Creature parent1 = _population.get(parent1Index).getValue1();
            Creature parent2 = _population.get(parent2Index).getValue1();
            Creature child = new Creature(parent1, parent2);
            newPopulation.add(new Pair<Double, Creature>(0.0, child));
        }
        _population = newPopulation;
        System.out.println("random new member\n"
                + _population.get(_population.size() - 1).getValue1().getBrain().render(RenderCode.BRAIN_WEIGHTS));
    }

    public static void main(String[] args)
    {
        Species species = new Species(0);
        species.setRandomInitialization(true);
        species.setInitialGenerationProbability(0);
        species.setMaximumFood(200);
        System.out.println("Species " + species);

        WorldVariables variables = new WorldVariables(species);
        variables.setInitialFoodGenerationProbability(0);

        GeneticAlgorithmRunner runner = new GeneticAlgorithmRunner(species, new TimeToExtinctionFF(30, variables, 100));
        runner.run();
    }
}
