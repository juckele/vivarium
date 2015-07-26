package com.johnuckele.vivarium.web.client;


public class JSONDecoder
{

    public static WebWorld convertJSONToWorld(String jsonString)
    {
        // TODO : FIX SERIALIZATION / DESERIALIZATION
        /*
         * JSONObject worldJSON = JSONParser.parseStrict(jsonString).isObject(); // Restore WorldVariables object
         * JSONObject worldVariablesJSON = worldJSON.get("worldVariables") .isObject(); WorldVariables worldVariables =
         * JSONDecoder .convertJSONtoWorldVariables(worldVariablesJSON); WebWorld w = new WebWorld(0, worldVariables);
         * // Restore basic values int worldDimensions = (int) worldJSON.get("worldDimensions").isNumber()
         * .doubleValue(); w.setWorldDimensions(worldDimensions); int tickCounter = (int)
         * worldJSON.get("tickCounter").isNumber() .doubleValue(); w.setTickCounter(tickCounter); int maximumCreatureID
         * = (int) worldJSON.get("maximumCreatureID") .isNumber().doubleValue();
         * w.setMaximumCreatureID(maximumCreatureID); // Populate data structures JSONArray worldObjects =
         * worldJSON.get("worldObjects").isArray(); for (int r = 0; r < w.getWorldDimensions(); r++) { for (int c = 0; c
         * < w.getWorldDimensions(); c++) { JSONObject jsonObject = worldObjects.get( r * w.getWorldDimensions() +
         * c).isObject(); String worldObjectString = jsonObject.get("type").isString() .stringValue(); WorldObject
         * objectType = WorldObject .parseString(worldObjectString); if (objectType == WorldObject.CREATURE) { Creature
         * u = JSONDecoder.convertJSONToCreature(w, jsonObject); w.addCreatureAtPosition(u, r, c); } else {
         * w.setObject(objectType, r, c); } } } return w;
         */
        return null;
    }

    // private static WorldVariables convertJSONtoWorldVariables()// JSONObject worldVariablesJSON)
    // {
    // TODO : FIX SERIALIZATION / DESERIALIZATION
    /*
     * WorldVariables worldVariables = new WorldVariables(); String[] variables = WorldVariables.getVariablesNames();
     * for (int i = 0; i < variables.length; i++) { JSONValue variableJSON = worldVariablesJSON.get(variables[i]); if
     * (variableJSON != null && variableJSON.isString() != null) { worldVariables.setKeyValue(variables[i], variableJSON
     * .isString().stringValue()); } } return worldVariables;
     */
    // return null;
    // }

    // private static Creature convertJSONToCreature(World w)// , JSONObject creatureJSON)
    // {
    // TODO : FIX SERIALIZATION / DESERIALIZATION
    /*
     * int id = (int) creatureJSON.get("id").isNumber().doubleValue(); int r = (int)
     * creatureJSON.get("r").isNumber().doubleValue(); int c = (int) creatureJSON.get("c").isNumber().doubleValue();
     * Creature u = new Creature(w, id, r, c); double generation = creatureJSON.get("generation").isNumber()
     * .doubleValue(); u.setGeneration(generation); boolean isFemale = creatureJSON.get("isFemale").isBoolean()
     * .booleanValue(); u.setIsFemale(isFemale); double randomSeed = creatureJSON.get("randomSeed").isNumber()
     * .doubleValue(); u.setRandomSeed(randomSeed); int age = (int) creatureJSON.get("age").isNumber().doubleValue();
     * u.setAge(age); int gestation = (int) creatureJSON.get("gestation").isNumber() .doubleValue();
     * u.setGestation(gestation); int food = (int) creatureJSON.get("food").isNumber().doubleValue(); u.setFood(food);
     * Direction facing = Direction.parseString(creatureJSON.get("facing") .isString().stringValue());
     * u.setFacing(facing); // Restore brain JSONObject brainJSON = creatureJSON.get("brain").isObject(); Brain brain =
     * JSONDecoder.convertJSONToBrain(w, brainJSON); u.setBrain(brain); // Restore fetus if present JSONValue fetusJSON
     * = creatureJSON.get("fetus"); if (fetusJSON != null && fetusJSON.isObject() != null) { Creature fetus =
     * convertJSONToCreature(w, fetusJSON.isObject()); u.setFetus(fetus); } return (u);
     */
    // return null;
    // }

    // private static Brain convertJSONToBrain(World w, JSONObject brainJSON) {
    // TODO : FIX SERIALIZATION / DESERIALIZATION
    /*
     * int inputCount = (int) brainJSON.get("inputCount").isNumber() .doubleValue(); int outputCount = (int)
     * brainJSON.get("outputCount").isNumber() .doubleValue(); int hiddenLayers = (int)
     * brainJSON.get("hiddenLayers").isNumber() .doubleValue(); Brain brain = new Brain(inputCount, outputCount,
     * hiddenLayers); JSONArray weightsJSON = brainJSON.get("weights").isArray(); double[][][] weights = new
     * double[weightsJSON.size()][][]; for (int i = 0; i < weights.length; i++) { JSONArray middleArrayJSON =
     * weightsJSON.get(i).isArray(); weights[i] = new double[middleArrayJSON.size()][]; for (int j = 0; j <
     * weights[i].length; j++) { JSONArray innerArrayJSON = middleArrayJSON.get(j).isArray(); weights[i][j] = new
     * double[innerArrayJSON.size()]; for (int k = 0; k < weights[i][j].length; k++) { weights[i][j][k] =
     * innerArrayJSON.get(k).isNumber() .doubleValue(); } } } // brain.setWeights(weights);
     */

    // return null;
    // }

}
