package com.johnuckele.vivarium.core;

import java.util.ArrayList;

import com.johnuckele.vivarium.util.Rand;

public class WorldPopulator
{
    private double             _wallProbability;
    private double             _foodProbability;
    private double             _creatureProbability;
    private ArrayList<Species> _species;

    public WorldPopulator()
    {

    }

    public void setSpecies(ArrayList<Species> species)
    {
        _species = species;
        _creatureProbability = 0;
        for (Species s : _species)
        {
            _creatureProbability += s.getInitialGenerationProbability();
        }
    }

    public void setWallProbability(double probability)
    {
        _wallProbability = probability;
    }

    public void setFoodProbability(double probability)
    {
        _foodProbability = probability;
    }

    public EntityType getNextEntityType()
    {
        double random = Rand.getRandomPositiveDouble();
        if (random < this._wallProbability)
        {
            return EntityType.WALL;
        }
        random -= this._wallProbability;

        if (random < this._foodProbability)
        {
            return EntityType.FOOD;
        }
        random -= this._foodProbability;

        if (random < this._creatureProbability)
        {
            return EntityType.CREATURE;
        }

        return EntityType.EMPTY;
    }

    public Species getNextCreatureSpecies()
    {
        double random = Rand.getRandomPositiveDouble() * _creatureProbability;
        for (Species s : _species)
        {
            if (random < s.getInitialGenerationProbability())
            {
                return s;
            }
            random -= s.getInitialGenerationProbability();
        }
        throw new IllegalStateException(
                "Species s.getInitialGenerationProbability() should sum to _creatureProbability");
    }

}
