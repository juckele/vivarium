package com.johnuckele.vivarium.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class WorldBlueprint
{
    // World Generation
    private static final double  FOOD_GENERATION_PROBABILITY             = 0.01;
    private static final double  INITIAL_FOOD_GENERATION_PROBABILITY     = 0.2;
    private static final double  INITIAL_WALL_GENERATION_PROBABILITY     = 0.1;

    // Simulation Details
    private static final boolean SOUND_ENABLED                           = false;

    // Serialization keys
    private static final String  SIZE_KEY                                = "size";
    private static final String  FOOD_GENERATION_PROBABILITY_KEY         = "foodGenerationProbability";
    private static final String  INITIAL_FOOD_GENERATION_PROBABILITY_KEY = "initialFoodGenerationProbability";
    private static final String  INITIAL_WALL_GENERATION_PROBABILITY_KEY = "initialWallGenerationProbability";

    // Simulation Details
    private static final String  SOUND_ENABLED_KEY                       = "soundEnabled";

    // World Generation
    private int                  _size;
    private double               _foodGenerationProbability              = FOOD_GENERATION_PROBABILITY;
    private double               _initialFoodGenerationProbability       = INITIAL_FOOD_GENERATION_PROBABILITY;
    private double               _initialWallGenerationProbability       = INITIAL_WALL_GENERATION_PROBABILITY;

    // Simulation Details
    private boolean              _soundEnabled                           = SOUND_ENABLED;

    private ArrayList<Species>   _species;

    // Private constructor for deserialization
    private WorldBlueprint()
    {
    }

    public WorldBlueprint(int size)
    {
        this(size, new Species());
    }

    public WorldBlueprint(int size, LinkedList<Species> species)
    {
        this._size = size;
        this._species = new ArrayList<Species>(species);
    }

    public WorldBlueprint(int size, Species species)
    {
        this._size = size;
        this._species = new ArrayList<Species>();
        this._species.add(species);
    }

    public double getFoodGenerationProbability()
    {
        return _foodGenerationProbability;
    }

    public double getInitialFoodGenerationProbability()
    {
        return _initialFoodGenerationProbability;
    }

    public double getInitialWallGenerationProbability()
    {
        return _initialWallGenerationProbability;
    }

    public int getSize()
    {
        return _size;
    }

    public boolean getSoundEnabled()
    {
        return this._soundEnabled;
    }

    public ArrayList<Species> getSpecies()
    {
        return this._species;
    }

    public void setFoodGenerationProbability(double p)
    {
        this._foodGenerationProbability = p;
    }

    public void setInitialFoodGenerationProbability(double p)
    {
        this._initialFoodGenerationProbability = p;
    }

    public void setInitialFoodGenerationProbability(int p)
    {
        this._initialFoodGenerationProbability = p;

    }

    public void setInitialWallGenerationProbability(double p)
    {
        this._initialWallGenerationProbability = p;
    }

    public void setSize(int _size)
    {
        this._size = _size;
    }

    public void setSoundEnabled(boolean soundEnabled)
    {
        this._soundEnabled = soundEnabled;
    }

    public void setSpecies(ArrayList<Species> _species)
    {
        this._species = _species;
    }

    // TODO : Remove this and replace with a serialize / deserialize routine
    public static WorldBlueprint createCopyOf(WorldBlueprint blueprint)
    {
        WorldBlueprint newprint = new WorldBlueprint();
        newprint._size = blueprint._size;
        newprint._foodGenerationProbability = blueprint._foodGenerationProbability;
        newprint._initialFoodGenerationProbability = blueprint._initialFoodGenerationProbability;
        newprint._initialWallGenerationProbability = blueprint._initialWallGenerationProbability;
        newprint._soundEnabled = blueprint._soundEnabled;
        newprint._species = new ArrayList<Species>();
        for (Species s : blueprint._species)
        {
            newprint._species.add(new Species(s));
        }
        return newprint;
    }

    public static WorldBlueprint deserialize(HashMap<String, String> blueprintValues)
    {
        // Make a new clean WorldBlueprint object to fill out
        WorldBlueprint blueprint = new WorldBlueprint();

        // Deserialize required variables (not checked so we die if they're missing)
        blueprint._size = Integer.parseInt(blueprintValues.remove(SIZE_KEY));

        // Deserialize optional variables, use defaults if not present
        blueprint._foodGenerationProbability = blueprintValues.containsKey(FOOD_GENERATION_PROBABILITY_KEY) ? Double
                .parseDouble(blueprintValues.remove(FOOD_GENERATION_PROBABILITY_KEY)) : FOOD_GENERATION_PROBABILITY;
        blueprint._initialFoodGenerationProbability = blueprintValues
                .containsKey(INITIAL_FOOD_GENERATION_PROBABILITY_KEY) ? Double.parseDouble(blueprintValues
                .remove(INITIAL_FOOD_GENERATION_PROBABILITY_KEY)) : INITIAL_FOOD_GENERATION_PROBABILITY;
        blueprint._initialWallGenerationProbability = blueprintValues
                .containsKey(INITIAL_WALL_GENERATION_PROBABILITY_KEY) ? Double.parseDouble(blueprintValues
                .remove(INITIAL_WALL_GENERATION_PROBABILITY_KEY)) : INITIAL_WALL_GENERATION_PROBABILITY;
        blueprint._soundEnabled = blueprintValues.containsKey(SOUND_ENABLED_KEY) ? Boolean.parseBoolean(blueprintValues
                .remove(SOUND_ENABLED_KEY)) : SOUND_ENABLED;

        // Die if there are unused variables
        if (!blueprintValues.isEmpty())
        {
            throw new IllegalArgumentException("WorldBlueprint could not deserialize keys " + blueprintValues.keySet());
        }

        // Build species by default
        Species s = new Species();
        blueprint._species = new ArrayList<Species>();
        blueprint._species.add(s);

        // Return the new object
        return blueprint;
    }
}
