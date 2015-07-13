package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.ArrayList;

public class WorldVariables implements Serializable
{
	private static final long	serialVersionUID									= 1L;

	/**
	 * Default values
	 */
	// Program Options
	private static final boolean	DEFAULT_REMEMBER_THE_DEAD						= false;
	private static final boolean	DEFAULT_KEEP_CENSUS_DATA						= false;
	private static final boolean	DEFAULT_KEEP_GENERATION_ACTION_PROFILE			= false;

	// World Gen
	private static final double	DEFAULT_FOOD_GENERATION_PROBABILITY					= 0.01;
	private static final double	DEFAULT_INITIAL_FOOD_GENERATION_PROBABILITY			= 0.2;
	private static final double	DEFAULT_INITIAL_WALL_GENERATION_PROBABILITY			= 0.1;

	private static final String[] VARIABLE_NAMES =
	{
		// Program Options
		"rememberTheDead",
		"keepCensusData",
		"keepGenerationActionProfile",
		// World Gen
		"foodGenerationProbability",
		"initialFoodGenerationProbability",
		"initialCreatureGenerationProbability",
		"initialWallGenerationProbability",
		// Creaturology
		"creatureMemoryUnitCount",
		"creatureSoundChannelCount",
		// Neurology
		"inheritanceGaussianMixRate",
		"inheritanceSinglePickRate",
		"mutationRateExponent",
		"mutationSmallScaleRate",
		"mutationRandomRate",
		"mutationFlipRate"
	};

	/**
	 * Instance variables
	 */
	// Program Options
	private boolean				_rememberTheDead;
	private boolean				_keepCensusData;
	private boolean				_keepGenerationActionProfile;

	// World Gen
	private double				_foodGenerationProbability;
	private double				_initialFoodGenerationProbability;
	private double				_initialWallGenerationProbability;

	private ArrayList<Species>	_species;

	public WorldVariables(int speciesCount)
	{
		// Program Options
		setRememberTheDead(DEFAULT_REMEMBER_THE_DEAD);
		setKeepCensusData(DEFAULT_KEEP_CENSUS_DATA);
		setKeepGenerationActionProfile(DEFAULT_KEEP_GENERATION_ACTION_PROFILE);
		
		// World Gen
		setFoodGenerationProbability(DEFAULT_FOOD_GENERATION_PROBABILITY);
		setInitialFoodGenerationProbability(DEFAULT_INITIAL_FOOD_GENERATION_PROBABILITY);
		setInitialWallGenerationProbability(DEFAULT_INITIAL_WALL_GENERATION_PROBABILITY);


		_species = new ArrayList<Species>(speciesCount);
		for(int i=0; i<speciesCount; i++)
		{
			_species.add(new Species());
		}
	}

	/**
	 * Returns the names in string form of all variables tracked by
	 * a WorldVariables objects.
	 * @return
	 */
	public static String[] getVariablesNames()
	{
		return WorldVariables.VARIABLE_NAMES;
	}

	public boolean getRememberTheDead()
	{
		return _rememberTheDead;
	}

	public void setRememberTheDead(boolean rememberTheDead)
	{
		_rememberTheDead = rememberTheDead;
	}

	public boolean getKeepCensusData()
	{
		return _keepCensusData;
	}

	public void setKeepCensusData(boolean keepCensusData)
	{
		_keepCensusData = keepCensusData;
	}

	public boolean getKeepGenerationActionProfile()
	{
		return _keepGenerationActionProfile;
	}

	public void setKeepGenerationActionProfile(boolean keepGenerationActionProfile)
	{
		_keepGenerationActionProfile = keepGenerationActionProfile;
	}

	public double getFoodGenerationProbability()
	{
		return _foodGenerationProbability;
	}

	public void setFoodGenerationProbability(double foodGenerationProbability)
	{
		this._foodGenerationProbability = foodGenerationProbability;
	}

	public double getInitialFoodGenerationProbability()
	{
		return _initialFoodGenerationProbability;
	}

	public void setInitialFoodGenerationProbability(double _initialFoodGenerationProbability)
	{
		this._initialFoodGenerationProbability = _initialFoodGenerationProbability;
	}

	public double getInitialWallGenerationProbability()
	{
		return _initialWallGenerationProbability;
	}

	public void setInitialWallGenerationProbability(double _initialWallGenerationProbability)
	{
		this._initialWallGenerationProbability = _initialWallGenerationProbability;
	}

	public void setKeyValue(String key, String value)
	{
		switch(key)
		{
			// Program Options
			case "rememberTheDead":
				this.setRememberTheDead(Boolean.parseBoolean(value));
				break;
			case "keepCensusData":
				this.setKeepCensusData(Boolean.parseBoolean(value));
				break;
			case "keepGenerationActionProfile":
				this.setKeepGenerationActionProfile(Boolean.parseBoolean(value));
				break;
			// World Gen
			case "foodGenerationProbability":
				this.setFoodGenerationProbability(Double.parseDouble(value));
				break;
			case "initialFoodGenerationProbability":
				this.setInitialFoodGenerationProbability(Double.parseDouble(value));
				break;
			case "initialWallGenerationProbability":
				this.setInitialWallGenerationProbability(Double.parseDouble(value));
				break;
		}
	}

	public Species getSpecies(int i) {
		return _species.get(i);
	}

	public ArrayList<Species> getSpecies() {
		return _species;
	}
}
