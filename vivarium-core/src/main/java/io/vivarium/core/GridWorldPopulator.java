package io.vivarium.core;

import java.util.ArrayList;

import io.vivarium.util.Rand;

public class GridWorldPopulator
{
    public enum EntityType
    {
        CREATURE, ITEM, TERRAIN, VOID
    }

    private double _wallProbability;
    private double _foodGeneratorProbability;
    private double _flamethrowerProbability;
    private double _foodProbability;
    private double _creatureProbability;
    private ArrayList<CreatureBlueprint> _creatureBlueprints;

    public GridWorldPopulator()
    {

    }

    public void setCreatureBlueprints(ArrayList<CreatureBlueprint> creatureBlueprints)
    {
        _creatureBlueprints = creatureBlueprints;
        _creatureProbability = 0;
        for (CreatureBlueprint s : _creatureBlueprints)
        {
            _creatureProbability += s.getInitialGenerationProbability();
        }
    }

    public void setWallProbability(double probability)
    {
        _wallProbability = probability;
    }

    public void setFoodGeneratorProbability(double probability)
    {
        _foodGeneratorProbability = probability;
    }

    public void setFoodProbability(double probability)
    {
        _foodProbability = probability;
    }

    public void setFlamethrowerProbability(double probability)
    {
        _flamethrowerProbability = probability;
    }

    public EntityType getNextEntityType()
    {
        double random = Rand.getInstance().getRandomPositiveDouble();
        if (random < this._wallProbability)
        {
            return EntityType.TERRAIN;
        }
        random -= this._wallProbability;

        if (random < this._foodGeneratorProbability)
        {
            return EntityType.TERRAIN;
        }
        random -= this._foodGeneratorProbability;

        if (random < this._flamethrowerProbability)
        {
            return EntityType.TERRAIN;
        }
        random -= this._flamethrowerProbability;

        if (random < this._foodProbability)
        {
            return EntityType.ITEM;
        }
        random -= this._foodProbability;

        if (random < this._creatureProbability)
        {
            return EntityType.CREATURE;
        }

        return EntityType.VOID;
    }

    public TerrainType getTerrainType()
    {

        double normalizedFoodGeneratorProbablity = this._foodGeneratorProbability
                / (this._wallProbability + this._foodGeneratorProbability + this._flamethrowerProbability);
        double normalizedFlamethrowerProbablity = this._flamethrowerProbability
                / (this._wallProbability + this._foodGeneratorProbability + this._flamethrowerProbability);

        double random = Rand.getInstance().getRandomPositiveDouble();

        if (random < normalizedFoodGeneratorProbablity)
        {
            return TerrainType.FOOD_GENERATOR;
        }
        random -= normalizedFoodGeneratorProbablity;

        if (random < normalizedFlamethrowerProbablity)
        {
            return TerrainType.FLAMETHROWER;
        }

        return TerrainType.WALL;

    }

    public CreatureBlueprint getNextCreatureBlueprint()
    {
        double random = Rand.getInstance().getRandomPositiveDouble() * _creatureProbability;
        for (CreatureBlueprint s : _creatureBlueprints)
        {
            if (random < s.getInitialGenerationProbability())
            {
                return s;
            }
            random -= s.getInitialGenerationProbability();
        }
        // If we fall through the for loop returns, it's because we've fundamentally screwed up our math.
        throw new IllegalStateException(
                "CreatureBlueprint s.getInitialGenerationProbability() should sum to " + _creatureProbability);
    }

}
