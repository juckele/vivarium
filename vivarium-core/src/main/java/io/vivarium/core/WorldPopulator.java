package io.vivarium.core;

import java.util.ArrayList;

import io.vivarium.util.Rand;

public class WorldPopulator
{
    private double _wallProbability;
    private double _foodProbability;
    private double _creatureProbability;
    private ArrayList<CreatureBlueprint> _species;

    public WorldPopulator()
    {

    }

    public void setSpecies(ArrayList<CreatureBlueprint> species)
    {
        _species = species;
        _creatureProbability = 0;
        for (CreatureBlueprint s : _species)
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
        double random = Rand.getInstance().getRandomPositiveDouble();
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

    public CreatureBlueprint getNextCreatureSpecies()
    {
        double random = Rand.getInstance().getRandomPositiveDouble() * _creatureProbability;
        for (CreatureBlueprint s : _species)
        {
            if (random < s.getInitialGenerationProbability())
            {
                return s;
            }
            random -= s.getInitialGenerationProbability();
        }
        // If we fall through the for loop returns, it's because we've fundamentally screwed up our math.
        throw new IllegalStateException(
                "Species s.getInitialGenerationProbability() should sum to " + _creatureProbability);
    }

}
