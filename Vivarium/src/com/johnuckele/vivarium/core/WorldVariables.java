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

	// World Gen
	private static final double	DEFAULT_FOOD_GENERATION_PROBABILITY					= 0.01;
	private static final double	DEFAULT_INITIAL_FOOD_GENERATION_PROBABILITY			= 0.2;
	private static final double	DEFAULT_INITIAL_UCKELEOID_GENERATION_PROBABILITY	= 0.2;
	private static final double	DEFAULT_INITIAL_WALL_GENERATION_PROBABILITY			= 0.1;

	// Uckeleoid Neurology
	private static final int	DEFAULT_UCKELEOID_MEMORY_UNIT_COUNT					= 0;
	private static final double	DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE				= 0.8;
	private static final double	DEFAULT_INHERITANCE_SINGLE_PICK_RATE				= 0.2;
	private static final double	DEFAULT_MUTATION_RATE_EXPONENT						= -7;
	private static final double	DEFAULT_MUTATION_SMALL_SCALE_RATE					= 0.5;
	private static final double	DEFAULT_MUTATION_RANDOM_RATE						= 0.25;
	private static final double	DEFAULT_MUTATION_FLIP_RATE							= 0.25;

	/**
	 * Instance variables
	 */
	// Program Options
	private boolean				_rememberTheDead;

	// World Gen
	private double				_foodGenerationProbability;
	private double				_initialFoodGenerationProbability;
	private double				_initialUckeleoidGenerationProbability;
	private double				_initialWallGenerationProbability;

	// Uckeleoid Neurology
	private int					_uckeleoidMemoryUnitCount;
	private double				_inheritanceGaussianMixRate;
	private double				_inheritanceSinglePickRate;
	private double				_mutationRate;
	private double				_mutationRateExponent;
	private double				_mutationSmallScaleRate;
	private double				_mutationRandomRate;
	private double				_mutationFlipRate;

	public WorldVariables()
	{
		setRememberTheDead(DEFAULT_REMEMBER_THE_DEAD);
		
		setFoodGenerationProbability(DEFAULT_FOOD_GENERATION_PROBABILITY);
		setInitialFoodGenerationProbability(DEFAULT_INITIAL_FOOD_GENERATION_PROBABILITY);
		setInitialUckeleoidGenerationProbability(DEFAULT_INITIAL_UCKELEOID_GENERATION_PROBABILITY);
		setInitialWallGenerationProbability(DEFAULT_INITIAL_WALL_GENERATION_PROBABILITY);

		setUckeleoidMemoryUnitCount(DEFAULT_UCKELEOID_MEMORY_UNIT_COUNT);
		setInheritanceGaussianMixRate(DEFAULT_INHERITANCE_GAUSSIAN_MIX_RATE);
		setInheritanceSinglePickRate(DEFAULT_INHERITANCE_SINGLE_PICK_RATE);
		setMutationRateExponent(DEFAULT_MUTATION_RATE_EXPONENT);
		setMutationSmallScaleRate(DEFAULT_MUTATION_SMALL_SCALE_RATE);
		setMutationRandomRate(DEFAULT_MUTATION_RANDOM_RATE);
		setMutationFlipRate(DEFAULT_MUTATION_FLIP_RATE);
	}

	public boolean getRememberTheDead()
	{
		return _rememberTheDead;
	}

	public void setRememberTheDead(boolean rememberTheDead)
	{
		_rememberTheDead = rememberTheDead;
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

	public void setUckeleoidMemoryUnitCount(int _uckeleoidMemoryUnitCount)
	{
		this._uckeleoidMemoryUnitCount = _uckeleoidMemoryUnitCount;
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
