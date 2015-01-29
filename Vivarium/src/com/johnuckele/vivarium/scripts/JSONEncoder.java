package com.johnuckele.vivarium.scripts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.core.UckeleoidBrain;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;

public class JSONEncoder
{
	static public JSONObject convertWorldToJSON(World w) throws JSONException
	{
		JSONObject worldJSON = new JSONObject();
		// Store basic values
		worldJSON.put("maximumUckeleoidID", w.getMaximumUckeleoidID());
		worldJSON.put("worldDimensions", w.getWorldDimensions());

		// Store world variables
		JSONObject worldVariableJSON = convertWorldVariablesToJSON(w.getWorldVariables());
		worldJSON.put("worldVariables",  worldVariableJSON);

		JSONArray worldObjects = new JSONArray();
		// Store what lives where
		for(int r = 0; r < w.getWorldDimensions(); r++)
		{
			for(int c = 0; c < w.getWorldDimensions(); c++)
			{
				if(w.getWorldObject(r, c) == WorldObject.UCKELEOID)
				{
					JSONObject worldObject = convertUckeleoidToJSON(w.getUckeleoid(r, c));
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

		// world gen
		worldVariablesJSON.put("foodGenerationProbability", worldVariables.getFoodGenerationProbability());
		worldVariablesJSON.put("initialFoodGenerationProbability", worldVariables.getInitialFoodGenerationProbability());
		worldVariablesJSON.put("initialUckeleoidGenerationProbability", worldVariables.getInitialUckeleoidGenerationProbability());
		worldVariablesJSON.put("initialWallGenerationProbability", worldVariables.getInitialWallGenerationProbability());
		
		// Uckeleoid Neurology
		worldVariablesJSON.put("uckeleoidMemoryUnitCount", worldVariables.getUckeleoidMemoryUnitCount());
		worldVariablesJSON.put("inheritanceGaussianMixRate", worldVariables.getInheritanceGaussianMixRate());
		worldVariablesJSON.put("inheritanceSinglePickRate", worldVariables.getInheritanceSinglePickRate());
		worldVariablesJSON.put("mutationRate", worldVariables.getMutationRate());
		worldVariablesJSON.put("mutationSmallScaleRate", worldVariables.getMutationSmallScaleRate());
		worldVariablesJSON.put("mutationRandomRate", worldVariables.getMutationRandomRate());
		worldVariablesJSON.put("mutationFlipRate", worldVariables.getMutationFlipRate());

		return worldVariablesJSON;
	}

	// Unsaved crap
	/*
	 * private UckeleoidBrain _brain; private double[] _memoryUnits;
	 * 
	 * private UckeleoidAction _action;
	 */
	private static JSONObject convertUckeleoidToJSON(Uckeleoid u) throws JSONException
	{
		JSONObject uckeleoidJSON = new JSONObject();
		uckeleoidJSON.put("type", "" + WorldObject.UCKELEOID);

		// Store basic values
		uckeleoidJSON.put("id", u.getID());
		uckeleoidJSON.put("r", u.getR());
		uckeleoidJSON.put("c", u.getC());
		uckeleoidJSON.put("generation", u.getGeneration());
		uckeleoidJSON.put("isFemale", u.getIsFemale());
		uckeleoidJSON.put("randomSeed", u.getRandomSeed());
		uckeleoidJSON.put("age", u.getAge());
		uckeleoidJSON.put("gestation", u.getGestation());
		uckeleoidJSON.put("food", u.getFood());
		uckeleoidJSON.put("facing", u.getFacing());

		// Store brain
		JSONObject brainJSON = convertUckeleoidBrainToJSON(u.getBrain());
		uckeleoidJSON.put("brain", brainJSON);

		// Store fetus if present
		Uckeleoid fetus = u.getFetus();
		if(fetus != null)
		{
			JSONObject fetusJSON = convertUckeleoidToJSON(fetus);
			uckeleoidJSON.put("fetus", fetusJSON);
		}

		return(uckeleoidJSON);
	}

	private static JSONObject convertUckeleoidBrainToJSON(UckeleoidBrain brain) throws JSONException
	{
		JSONObject brainJSON = new JSONObject();
		
		brainJSON.put("hiddenLayers", brain.getHiddenLayers());
		brainJSON.put("inputCount", brain.getInputCount());
		brainJSON.put("outputCount", brain.getOutputCount());

		double[][][] weights = brain.getWeights();
		JSONArray weightsJSON = new JSONArray();
		for(int i = 0; i < weights.length; i++)
		{
			JSONArray middleArrayJSON = new JSONArray();
			for(int j = 0; j < weights[i].length; j++)
			{
				JSONArray innerArrayJSON = new JSONArray();
				for(int k = 0; k < weights[i][j].length; k++)
				{
					innerArrayJSON.put(weights[i][j][k]);
				}
				middleArrayJSON.put(innerArrayJSON);
			}
			weightsJSON.put(middleArrayJSON);
		}
		brainJSON.put("weights", weightsJSON);

		return(brainJSON);
	}
}
