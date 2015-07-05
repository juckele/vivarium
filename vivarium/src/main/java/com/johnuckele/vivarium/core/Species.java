package com.johnuckele.vivarium.core;

public class Species {
	// Creature Constants
	private static final double	DEFAULT_FEMALE_THRESHOLD	= 0.6;
	private static final int	MAXIMUM_AGE					= 20000;
	private static final int	MAXIMUM_GESTATION			= 2000;
	private static final int	MAXIMUM_FOOD				= 2000;

	private static final int	BRAIN_INPUTS 				= 5;
	private static final int	BRAIN_OUTPUTS	 			= 6;
	
	private double _femaleThreshold = DEFAULT_FEMALE_THRESHOLD;
	private int _maximumAge = MAXIMUM_AGE;
	private int _maximumGestation = MAXIMUM_GESTATION;
	private int _maximumFood = MAXIMUM_FOOD;
	
	private int _brainInputs = BRAIN_INPUTS;
	private int _brainOutputs = BRAIN_OUTPUTS;
	
//	private Action[] _involuntaryActions;
//	private Action[] _voluntaryActions;

	public Species() {
		
	}

	public int getBrainInputs() {
		return _brainInputs;
	}

	public int getBrainOutputs() {
		return _brainOutputs;
	}

	public double getFemaleThreshold() {
		return _femaleThreshold;
	}

	public int getMaximumAge() {
		return _maximumAge;
	}

	public int getMaximumFood() {
		return _maximumFood;
	}
	public int getMaximumGestation() {
		return _maximumGestation;
	}


}
