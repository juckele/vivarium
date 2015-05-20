package com.johnuckele.vivarium.core;

import java.io.Serializable;

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
	private static final double	DEFAULT_INITIAL_UCKELEOID_GENERATION_PROBABILITY	= 0.2;
	private static final double	DEFAULT_INITIAL_WALL_GENERATION_PROBABILITY			= 0.1;

	// Uckeleoid Neurology
	private static final int	DEFAULT_UCKELEOID_MEMORY_UNIT_COUNT					= 0;
	private static final int	DEFAULT_UCKELEOID_SOUND_CHANNEL_COUNT				= 0;
	private static final double	DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE				= 0.8;
	private static final double	DEFAULT_INHERITANCE_SINGLE_PICK_RATE				= 0.2;
	private static final double	DEFAULT_MUTATION_RATE_EXPONENT						= -7;
	private static final double	DEFAULT_MUTATION_SMALL_SCALE_RATE					= 0.5;
	private static final double	DEFAULT_MUTATION_RANDOM_RATE						= 0.25;
	private static final double	DEFAULT_MUTATION_FLIP_RATE							= 0.25;

	private static final String[] VARIABLE_NAMES =
	{
		// Program Options
		"rememberTheDead",
		"keepCensusData",
		"keepGenerationActionProfile",
		// World Gen
		"foodGenerationProbability",
		"initialFoodGenerationProbability",
		"initialUckeleoidGenerationProbability",
		"initialWallGenerationProbability",
		// Uckeleoid Neurology
		"uckeleoidMemoryUnitCount",
		"uckeleoidSoundChannelCount",
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
	private double				_initialUckeleoidGenerationProbability;
	private double				_initialWallGenerationProbability;

	// Uckeleoid Neurology
	private int					_uckeleoidMemoryUnitCount;
	private int					_uckeleoidSoundChannelCount;
	private double				_inheritanceGaussianMixRate;
	private double				_inheritanceSinglePickRate;
	private double				_mutationRate;
	private double				_mutationRateExponent;
	private double				_mutationSmallScaleRate;
	private double				_mutationRandomRate;
	private double				_mutationFlipRate;

	public WorldVariables()
	{
		// Program Options
		setRememberTheDead(DEFAULT_REMEMBER_THE_DEAD);
		setKeepCensusData(DEFAULT_KEEP_CENSUS_DATA);
		setKeepGenerationActionProfile(DEFAULT_KEEP_GENERATION_ACTION_PROFILE);
		
		// World Gen
		setFoodGenerationProbability(DEFAULT_FOOD_GENERATION_PROBABILITY);
		setInitialFoodGenerationProbability(DEFAULT_INITIAL_FOOD_GENERATION_PROBABILITY);
		setInitialUckeleoidGenerationProbability(DEFAULT_INITIAL_UCKELEOID_GENERATION_PROBABILITY);
		setInitialWallGenerationProbability(DEFAULT_INITIAL_WALL_GENERATION_PROBABILITY);

		// Uckeleoid Neurology
		setUckeleoidMemoryUnitCount(DEFAULT_UCKELEOID_MEMORY_UNIT_COUNT);
		setUckeleoidSoundChannelCount(DEFAULT_UCKELEOID_SOUND_CHANNEL_COUNT);
		setInheritanceGaussianMixRate(DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE);
		setInheritanceSinglePickRate(DEFAULT_INHERITANCE_SINGLE_PICK_RATE);
		setMutationRateExponent(DEFAULT_MUTATION_RATE_EXPONENT);
		setMutationSmallScaleRate(DEFAULT_MUTATION_SMALL_SCALE_RATE);
		setMutationRandomRate(DEFAULT_MUTATION_RANDOM_RATE);
		setMutationFlipRate(DEFAULT_MUTATION_FLIP_RATE);
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

	public double getInitialUckeleoidGenerationProbability()
	{
		return _initialUckeleoidGenerationProbability;
	}

	public void setInitialUckeleoidGenerationProbability(double _initialUckeleoidGenerationProbability)
	{
		this._initialUckeleoidGenerationProbability = _initialUckeleoidGenerationProbability;
	}

	public double getInitialWallGenerationProbability()
	{
		return _initialWallGenerationProbability;
	}

	public void setInitialWallGenerationProbability(double _initialWallGenerationProbability)
	{
		this._initialWallGenerationProbability = _initialWallGenerationProbability;
	}

	public int getUckeleoidMemoryUnitCount()
	{
		return _uckeleoidMemoryUnitCount;
	}

	public void setUckeleoidMemoryUnitCount(int uckeleoidMemoryUnitCount)
	{
		this._uckeleoidMemoryUnitCount = uckeleoidMemoryUnitCount;
	}

	public int getUckeleoidSoundChannelCount()
	{
		return _uckeleoidSoundChannelCount;
	}

	public void setUckeleoidSoundChannelCount(int uckeleoidSoundChannelCount)
	{
		this._uckeleoidSoundChannelCount = uckeleoidSoundChannelCount;
	}

	public double getInheritanceGaussianMixRate()
	{
		return _inheritanceGaussianMixRate;
	}

	public void setInheritanceGaussianMixRate(double inheritanceGaussianMixRate)
	{
		this._inheritanceGaussianMixRate = inheritanceGaussianMixRate;
	}

	public double getInheritanceSinglePickRate()
	{
		return _inheritanceSinglePickRate;
	}

	public void setInheritanceSinglePickRate(double inheritanceSinglePickRate)
	{
		this._inheritanceSinglePickRate = inheritanceSinglePickRate;
	}

	public double getMutationRate()
	{
		return _mutationRate;
	}

	public double getMutationRateExponent()
	{
		return _mutationRateExponent;
	}

	public void setMutationRateExponent(double mutationRateExponent)
	{
		this._mutationRateExponent = mutationRateExponent;
		this._mutationRate = Math.pow(2, mutationRateExponent);
	}

	public double getMutationSmallScaleRate()
	{
		return _mutationSmallScaleRate;
	}

	public void setMutationSmallScaleRate(double mutationSmallScaleRate)
	{
		this._mutationSmallScaleRate = mutationSmallScaleRate;
	}

	public double getMutationRandomRate()
	{
		return _mutationRandomRate;
	}

	public void setMutationRandomRate(double mutationRandomRate)
	{
		this._mutationRandomRate = mutationRandomRate;
	}

	public double getMutationFlipRate()
	{
		return _mutationFlipRate;
	}

	public void setMutationFlipRate(double mutationFlipRate)
	{
		this._mutationFlipRate = mutationFlipRate;
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
			case "initialUckeleoidGenerationProbability":
				this.setInitialUckeleoidGenerationProbability(Double.parseDouble(value));
				break;
			case "initialWallGenerationProbability":
				this.setInitialWallGenerationProbability(Double.parseDouble(value));
				break;
			// Uckeleoid Neurology
			case "uckeleoidMemoryUnitCount":
				this.setUckeleoidMemoryUnitCount(Integer.parseInt(value));
				break;
			case "uckeleoidSoundChannelCount":
				this.setUckeleoidSoundChannelCount(Integer.parseInt(value));
				break;
			case "inheritanceGaussianMixRate":
				this.setInheritanceGaussianMixRate(Double.parseDouble(value));
				break;
			case "inheritanceSinglePickRate":
				this.setInheritanceSinglePickRate(Double.parseDouble(value));
				break;
			case "mutationRateExponent":
				this.setMutationRateExponent(Double.parseDouble(value));
				break;
			case "mutationSmallScaleRate":
				this.setMutationSmallScaleRate(Double.parseDouble(value));
				break;
			case "mutationRandomRate":
				this.setMutationRandomRate(Double.parseDouble(value));
				break;
			case "mutationFlipRate":
				this.setMutationFlipRate(Double.parseDouble(value));
				break;
			default:
				throw new Error("Unhandled case with key: "+key+", value: "+value);
		}
	}
}
