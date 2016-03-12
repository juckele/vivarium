package io.vivarium.ga;

import java.util.ArrayList;
import java.util.Collections;

import org.javatuples.Pair;

import io.vivarium.core.Blueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.Species;
import io.vivarium.core.processor.ProcessorArchitecture;
import io.vivarium.util.Rand;
import io.vivarium.visualization.RenderCode;

/**
 * A class for running a standard genetic algorithm across a population of creatures testing them with one or more
 * {@code FitnessFunction}.
 */
public class GeneticAlgorithmRunner
{
    Species _species;
    int _currentGeneration = 1;
    int _generations = 100;
    int _populationSize = 100;
    ArrayList<Pair<Double, Creature>> _population = new ArrayList<>(_populationSize);
    private FitnessFunction _fitnessFunction;

    public GeneticAlgorithmRunner(Species species, FitnessFunction fitnessFunction)
    {
        this._fitnessFunction = fitnessFunction;

        this._species = species;

        buildInitialPopulation();
    }

    private void buildInitialPopulation()
    {
        for (int i = 0; i < _populationSize; i++)
        {
            Creature c = new Creature(_species);
            System.out.println(c.getProcessor().render(RenderCode.PROCESSOR_WEIGHTS));
            _population.add(new Pair<>(0.0, c));
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
        Pair<Double, Creature> updatedPopulationMember = new Pair<>(fitness, c);
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
        System.out.println("1st Best member\n" + _population.get(_population.size() - 1).getValue1().getProcessor()
                .render(RenderCode.PROCESSOR_WEIGHTS));
        ArrayList<Pair<Double, Creature>> newPopulation = new ArrayList<>(_populationSize);
        double fitnessSum = 0;
        for (int i = 0; i < _populationSize; i++)
        {
            fitnessSum += _population.get(i).getValue0();
        }
        System.out.println("Total fitness is " + fitnessSum);
        for (int i = 0; i < _populationSize; i++)
        {
            int parent1Index = Math.max(Rand.getInstance().getRandomInt(_populationSize - i) + i,
                    Rand.getInstance().getRandomInt(_populationSize - i) + i);
            int parent2Index = Math.max(Rand.getInstance().getRandomInt(_populationSize - i) + i,
                    Rand.getInstance().getRandomInt(_populationSize - i) + i);
            Creature parent1 = _population.get(parent1Index).getValue1();
            Creature parent2 = _population.get(parent2Index).getValue1();
            Creature child = new Creature(parent1, parent2);
            newPopulation.add(new Pair<>(0.0, child));
        }
        _population = newPopulation;
        System.out.println("random new member\n" + _population.get(_population.size() - 1).getValue1().getProcessor()
                .render(RenderCode.PROCESSOR_WEIGHTS));
    }

    public static void main(String[] args)
    {
        ProcessorArchitecture processorArchitecture = ProcessorArchitecture.makeDefault();
        processorArchitecture.setRandomInitialization(true);

        Species species = Species.makeDefault();
        species.setProcessorArchitecture(processorArchitecture);
        species.setInitialGenerationProbability(0);
        species.setMaximumFood(200);
        System.out.println("Species " + species);

        Blueprint blueprint = Blueprint.makeDefault();
        blueprint.setSize(30);
        ArrayList<Species> speciesList = new ArrayList<>();
        blueprint.setSpecies(speciesList);
        blueprint.setInitialFoodGenerationProbability(0);

        int tenLifespans = species.getMaximumAge() * 10;
        GeneticAlgorithmRunner runner = new GeneticAlgorithmRunner(species,
                new TimeToExtinctionFF(blueprint, 100, tenLifespans));
        runner.run();
    }
}
