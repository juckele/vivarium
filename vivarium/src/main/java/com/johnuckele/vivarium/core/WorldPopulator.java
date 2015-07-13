package com.johnuckele.vivarium.core;

import java.util.ArrayList;

import com.johnuckele.vivarium.util.Rand;

public class WorldPopulator
{
    private double _wallProbability;
    private double _foodProbability;
    private double _creatureProbability;
    private ArrayList<Species> _species;
    
    public WorldPopulator()
    {
        
    }

    public void setSpecies(ArrayList<Species> species)
    {
        _species = species;
        _creatureProbability = 0;
        for(Species s : _species)
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

    public WorldObject getNextWorldObject()
    {
        double random = Rand.getRandomPositiveDouble();
        if(random < this._wallProbability) {
            return WorldObject.WALL;
        }
        random -= this._wallProbability;

        if (random < this._foodProbability) {
            return WorldObject.FOOD;
        }
        random -= this._foodProbability;
        
        if (random < this._creatureProbability) {
            return WorldObject.CREATURE;
        }

        return WorldObject.EMPTY;
    }

    public Species getNextCreatureSpecies()
    {
        double random = Rand.getRandomPositiveDouble() * _creatureProbability ;
        for(Species s : _species)
        {
            if( random < s.getInitialGenerationProbability() )
            {
                return s;
            }
            random -= s.getInitialGenerationProbability();
        }
        throw new IllegalStateException("Species s.getInitialGenerationProbability() should sum to _creatureProbability");
    }

}
