package com.johnuckele.vivarium.web.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.johnuckele.vivarium.core.Direction;
import com.johnuckele.vivarium.core.Uckeleoid;
import com.johnuckele.vivarium.core.UckeleoidBrain;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;

public class JSONDecoder
{

	public static WebWorld convertJSONToWorld(String jsonString)
	{
		JSONObject worldJSON = JSONParser.parseStrict(jsonString).isObject();

		// Restore WorldVariables object
		JSONObject worldVariablesJSON = worldJSON.get("worldVariables").isObject();
		WorldVariables worldVariables = JSONDecoder.convertJSONtoWorldVariables(worldVariablesJSON);
		WebWorld w = new WebWorld(0, worldVariables);

		// Restore basic values
		int worldDimensions = (int)worldJSON.get("worldDimensions").isNumber().doubleValue();
		w.setWorldDimensions(worldDimensions);
		int tickCounter = (int)worldJSON.get("tickCounter").isNumber().doubleValue();
		w.setTickCounter(tickCounter);
		int maximumUckeleoidID = (int)worldJSON.get("maximumUckeleoidID").isNumber().doubleValue();
		w.setMaximumUckeleoidID(maximumUckeleoidID);

		// Populate data structures
		JSONArray worldObjects = worldJSON.get("worldObjects").isArray();
		for(int r = 0; r < w.getWorldDimensions(); r++)
		{
			for(int c = 0; c < w.getWorldDimensions(); c++)
			{
				JSONObject jsonObject = worldObjects.get(r * w.getWorldDimensions() + c).isObject();
				String worldObjectString = jsonObject.get("type").isString().stringValue();
				WorldObject objectType = WorldObject.parseString(worldObjectString);
				if(objectType == WorldObject.UCKELEOID)
				{
					Uckeleoid u = JSONDecoder.convertJSONToUckeleoid(w, jsonObject);
					w.addUckeleoidAtPosition(u, r, c);
				}
				else
				{
					w.setObject(objectType, r, c);
				}
			}
		}

		return w;
	}

	private static WorldVariables convertJSONtoWorldVariables(JSONObject worldVariablesJSON)
	{
		WorldVariables worldVariables = new WorldVariables();
		String[] variables = WorldVariables.getVariablesNames();
		for(int i=0; i<variables.length; i++)
		{
			JSONValue variableJSON = worldVariablesJSON.get(variables[i]);
			if(variableJSON != null && variableJSON.isString() != null)
			{
				worldVariables.setKeyValue(variables[i], variableJSON.isString().stringValue());
			}
		}

		return worldVariables;
	}

	private static Uckeleoid convertJSONToUckeleoid(World w, JSONObject uckeleoidJSON)
	{
		int id = (int)uckeleoidJSON.get("id").isNumber().doubleValue();
		int r = (int)uckeleoidJSON.get("r").isNumber().doubleValue();
		int c = (int)uckeleoidJSON.get("c").isNumber().doubleValue();
		Uckeleoid u = new Uckeleoid(w, id, r, c);

		double generation = uckeleoidJSON.get("generation").isNumber().doubleValue();
		u.setGeneration(generation);
		boolean isFemale = uckeleoidJSON.get("isFemale").isBoolean().booleanValue();
		u.setIsFemale(isFemale);
		double randomSeed = uckeleoidJSON.get("randomSeed").isNumber().doubleValue();
		u.setRandomSeed(randomSeed);
		int age = (int)uckeleoidJSON.get("age").isNumber().doubleValue();
		u.setAge(age);
		int gestation = (int)uckeleoidJSON.get("gestation").isNumber().doubleValue();
		u.setGestation(gestation);
		int food = (int)uckeleoidJSON.get("food").isNumber().doubleValue();
		u.setFood(food);
		Direction facing = Direction.parseString(uckeleoidJSON.get("facing").isString().stringValue());
		u.setFacing(facing);
		
		// Restore brain
		JSONObject brainJSON = uckeleoidJSON.get("brain").isObject();
		UckeleoidBrain brain = JSONDecoder.convertJSONToUckeleoidBrain(w, brainJSON);
		u.setBrain(brain);

		// Restore fetus if present
		JSONValue fetusJSON = uckeleoidJSON.get("fetus");
		if(fetusJSON != null && fetusJSON.isObject() != null)
		{
			Uckeleoid fetus = convertJSONToUckeleoid(w, fetusJSON.isObject());
			u.setFetus(fetus);
		}
		
		return(u);
	}

	private static UckeleoidBrain convertJSONToUckeleoidBrain(World w, JSONObject brainJSON)
	{
		int inputCount = (int)brainJSON.get("inputCount").isNumber().doubleValue();
		int outputCount = (int)brainJSON.get("outputCount").isNumber().doubleValue();
		int hiddenLayers = (int)brainJSON.get("hiddenLayers").isNumber().doubleValue();
		
		UckeleoidBrain brain = new UckeleoidBrain(inputCount, outputCount, hiddenLayers);

		JSONArray weightsJSON = brainJSON.get("weights").isArray();
		double[][][] weights = new double[weightsJSON.size()][][];
		for(int i = 0; i < weights.length; i++)
		{
			JSONArray middleArrayJSON = weightsJSON.get(i).isArray();
			weights[i] = new double[middleArrayJSON.size()][];
			for(int j = 0; j < weights[i].length; j++)
			{
				JSONArray innerArrayJSON = middleArrayJSON.get(j).isArray();
				weights[i][j] = new double[innerArrayJSON.size()];
				for(int k = 0; k < weights[i][j].length; k++)
				{
					weights[i][j][k] = innerArrayJSON.get(k).isNumber().doubleValue();
				}
			}
		}
		brain.setWeights(weights);

		return brain;
	}

	
}
