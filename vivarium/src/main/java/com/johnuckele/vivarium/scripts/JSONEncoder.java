package com.johnuckele.vivarium.scripts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.johnuckele.vivarium.core.Creature;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;
import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;

public class JSONEncoder
{
    static public JSONObject convertWorldToJSON(World w) throws JSONException
    {
        JSONObject worldJSON = new JSONObject();
        // Store basic values
        worldJSON.put("maximumCreatureID", w.getMaximimCreatureID());
        worldJSON.put("tickCounter", w.getTickCounter());
        worldJSON.put("worldDimensions", w.getWorldDimensions());

        // Store world variables
        JSONObject worldVariableJSON = convertWorldVariablesToJSON(w.getWorldVariables());
        worldJSON.put("worldVariables", worldVariableJSON);

        JSONArray worldObjects = new JSONArray();
        // Store what lives where
        for (int r = 0; r < w.getWorldDimensions(); r++)
        {
            for (int c = 0; c < w.getWorldDimensions(); c++)
            {
                if (w.getWorldObject(r, c) == WorldObject.CREATURE)
                {
                    JSONObject worldObject = convertCreatureToJSON(w.getCreature(r, c));
                    worldObject.put("type", "" + w.getWorldObject(r, c));
                    worldObjects.put(worldObject);
                }
                else
                {
                    JSONObject worldObject = new JSONObject();
                    worldObject.put("type", "" + w.getWorldObject(r, c));
                    worldObjects.put(worldObject);
                }
            }
        }
        worldJSON.put("worldObjects", worldObjects);

        return worldJSON;
    }

    private static JSONObject convertWorldVariablesToJSON(WorldVariables worldVariables) throws JSONException
    {
        JSONObject worldVariablesJSON = new JSONObject();

        // TODO FIX THIS
        /*
         * // Program options worldVariablesJSON.put("rememberTheDead",
         * worldVariables.getRememberTheDead());
         * 
         * // World gen worldVariablesJSON.put("foodGenerationProbability",
         * worldVariables.getFoodGenerationProbability());
         * worldVariablesJSON.put("initialFoodGenerationProbability",
         * worldVariables.getInitialFoodGenerationProbability());
         * worldVariablesJSON.put("initialCreatureGenerationProbability",
         * worldVariables.getInitialCreatureGenerationProbability());
         * worldVariablesJSON.put("initialWallGenerationProbability",
         * worldVariables.getInitialWallGenerationProbability());
         * 
         * // Neurology worldVariablesJSON.put("creatureMemoryUnitCount",
         * worldVariables.getCreatureMemoryUnitCount());
         * worldVariablesJSON.put("inheritanceGaussianMixRate",
         * worldVariables.getInheritanceGaussianMixRate());
         * worldVariablesJSON.put("inheritanceSinglePickRate",
         * worldVariables.getInheritanceSinglePickRate());
         * worldVariablesJSON.put("mutationRateExponent",
         * worldVariables.getMutationRateExponent());
         * worldVariablesJSON.put("mutationSmallScaleRate",
         * worldVariables.getMutationSmallScaleRate());
         * worldVariablesJSON.put("mutationRandomRate",
         * worldVariables.getMutationRandomRate());
         * worldVariablesJSON.put("mutationFlipRate",
         * worldVariables.getMutationFlipRate());
         */

        return worldVariablesJSON;
    }

    // Unsaved crap
    /*
     * private Brain _brain; private double[] _memoryUnits;
     * 
     * private Action _action;
     */
    private static JSONObject convertCreatureToJSON(Creature u) throws JSONException
    {
        JSONObject creatureJSON = new JSONObject();
        creatureJSON.put("type", "" + WorldObject.CREATURE);

        // Store basic values
        creatureJSON.put("id", u.getID());
        creatureJSON.put("r", u.getR());
        creatureJSON.put("c", u.getC());
        creatureJSON.put("generation", u.getGeneration());
        creatureJSON.put("isFemale", u.getIsFemale());
        creatureJSON.put("randomSeed", u.getRandomSeed());
        creatureJSON.put("age", u.getAge());
        creatureJSON.put("gestation", u.getGestation());
        creatureJSON.put("food", u.getFood());
        creatureJSON.put("facing", u.getFacing());

        // Store brain
        // TODO: FIX ENCODING
        try
        {
            JSONObject brainJSON = convertBrainToJSON((NeuralNetworkBrain) u.getBrain());
            creatureJSON.put("brain", brainJSON);
        }
        catch (Error e)
        {
            throw new Error(
                    "Lazy programmer error, please implement JSON serialization and deserialization for multiple brain types");
        }

        // Store fetus if present
        Creature fetus = u.getFetus();
        if (fetus != null)
        {
            JSONObject fetusJSON = convertCreatureToJSON(fetus);
            creatureJSON.put("fetus", fetusJSON);
        }

        return (creatureJSON);
    }

    private static JSONObject convertBrainToJSON(NeuralNetworkBrain brain) throws JSONException
    {
        JSONObject brainJSON = new JSONObject();

        brainJSON.put("hiddenLayers", brain.getHiddenLayers());
        brainJSON.put("inputCount", brain.getInputCount());
        brainJSON.put("outputCount", brain.getOutputCount());

        double[][][] weights = brain.getWeights();
        JSONArray weightsJSON = new JSONArray();
        for (int i = 0; i < weights.length; i++)
        {
            JSONArray middleArrayJSON = new JSONArray();
            for (int j = 0; j < weights[i].length; j++)
            {
                JSONArray innerArrayJSON = new JSONArray();
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    innerArrayJSON.put(weights[i][j][k]);
                }
                middleArrayJSON.put(innerArrayJSON);
            }
            weightsJSON.put(middleArrayJSON);
        }
        brainJSON.put("weights", weightsJSON);

        return (brainJSON);
    }
}
