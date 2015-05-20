package com.johnuckele.vivarium.core;

import java.io.Serializable;

public class Uckeleoid implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long	serialVersionUID			= 4L;

	// Uckeleoid Constants
	private static final double	DEFAULT_FEMALE_THRESHOLD	= 0.6;
	private static final int	MAXIMUM_AGE					= 20000;
	private static final int	MAXIMUM_GESTATION			= 2000;
	private static final int	MAXIMUM_FOOD				= 2000;

	private static final int	BREEDING_FOOD_RATE			= -10;
	private static final int	EATING_FOOD_RATE			= 500;
	private static final int	MOVING_FOOD_RATE			= -1;
	private static final int	BASE_FOOD_RATE				= -1;
	private static final int	PREGNANT_FOOD_RATE			= -2;

	// Brain structure
	private static final int	BRAIN_INPUTS				= 5;
	private static final int	BRAIN_OUTPUTS				= 6;

	// Meta information
	private int					_id;
	private World				_world;
	private int					_r;
	private int					_c;
	private double				_generation;

	// Brain
	private UckeleoidBrain		_brain;
	private double[]			_inputs;
	private double[]			_memoryUnits;
	private double[]			_soundInputs;
	private double[]			_soundOutputs;

	// State
	private Gender				_gender;
	private double				_randomSeed;
	private int					_age;
	private int					_gestation;
	private int					_food;
	private Direction			_facing;
	private UckeleoidAction		_action;

	// Fetus
	private Uckeleoid			_fetus;

	// Action history
	private ActionProfile		_actionProfile;
	private ActionProfile		_generationGenderActionProfile;

	// Randomly initialized Uckeleoid
	public Uckeleoid(World world, int r, int c)
	{
		this(null, null, world, world.requestNewUckleoidID(), r, c);
	}

	public Uckeleoid(World world, int id, int r, int c)
	{
		this(null, null, world, id, r, c);
	}

	public Uckeleoid(Uckeleoid parent1, Uckeleoid parent2, World world)
	{
		this(parent1, parent2, world, world.requestNewUckleoidID(), 0, 0);
	}

	public Uckeleoid(Uckeleoid parent1, Uckeleoid parent2, World world, int id, int r, int c)
	{
		this._world = world;
		this._id = id;
		this._r = r;
		this._c = c;

		if(parent1 != null)
		{
			this._generation = UtilityFunctions.logarithmicAverage(parent1._generation, parent2._generation) + 1;
		}
		else
		{
			this._generation = 1;
		}

		// Create brain to control the Uckeleoid
		int memoryUnitCount = _world.getWorldVariables().getUckeleoidMemoryUnitCount();
		int soundChannelCount = _world.getWorldVariables().getUckeleoidSoundChannelCount();
		int totalBrainInputs = Uckeleoid.BRAIN_INPUTS + memoryUnitCount + soundChannelCount;
		int totalBrainOutputs = Uckeleoid.BRAIN_OUTPUTS + memoryUnitCount + soundChannelCount;
		
		if(parent1 != null && parent2 != null)
		{
			// Brain combined from genetic legacy
			this._brain = new UckeleoidBrain(_world, parent1._brain, parent2._brain);
		}
		else if(parent1 == null && parent1 == null)
		{
			// Create a new default brain
			this._brain = new UckeleoidBrain(_world, totalBrainInputs, totalBrainOutputs, 0);
		}
		else
		{
			throw new Error("Uckeleoid with single parent");
		}
		_inputs = new double[totalBrainInputs];
		_memoryUnits = new double[memoryUnitCount];
		_soundInputs = new double[soundChannelCount];
		_soundOutputs = new double[soundChannelCount];

		// Set gender
		double randomNumber = Math.random();
		if(randomNumber < DEFAULT_FEMALE_THRESHOLD)
		{
			this._gender = Gender.FEMALE;
		}
		else
		{
			this._gender = Gender.MALE;
		}

		// Set food trait preferences
		this._randomSeed = Math.random();

		// Set defaults
		this._age = 0;
		this._gestation = 0;
		this._food = Uckeleoid.MAXIMUM_FOOD;
		this._facing = Direction.NORTH;
		this._action = UckeleoidAction.REST;

		if(this._world.getWorldVariables().getKeepGenerationActionProfile())
		{
			this._actionProfile = new ActionProfile();
			this._generationGenderActionProfile = this._world.getActionProfileForGeneration((int) this._generation, this._gender);
		}
	}

	public void tick()
	{
		// Age
		this._age++;

		// Gestate if preggars
		if(this._gestation > 0)
		{
			this._gestation++;
			this._food += Uckeleoid.PREGNANT_FOOD_RATE;
		}
		else
		{
			this._food += Uckeleoid.BASE_FOOD_RATE;
		}

	}

	public UckeleoidAction getAction()
	{
		return(_action);
	}

	public void listenToUckeleoid(Uckeleoid u)
	{
		int distanceSquared = (this._r - u._r)*(this._r - u._r) + (this._c - u._c)*(this._c - u._c);
		for(int i=0; i < u._soundOutputs.length; i++)
		{
			this._soundInputs[i] += u._soundOutputs[i]/distanceSquared;
		}
	}

	public void planAction()
	{
		_action = determineAction();
	}

	private UckeleoidAction determineAction()
	{
		UckeleoidAction involuntaryAction = getInvoluntaryAction();
		if(involuntaryAction != null)
		{
			return(involuntaryAction);
		}
		else
		{
			// Hard coded inputs
			int facingR = this._r + Direction.getVerticalComponent(this._facing);
			int facingC = this._c + Direction.getHorizontalComponent(this._facing);
			WorldObject facingObject = this._world.getWorldObject(facingR, facingC);
			_inputs[0] = this._gender == Gender.FEMALE ? 1 : 0;
			_inputs[1] = facingObject == WorldObject.FOOD ? 1 : 0;
			_inputs[2] = facingObject == WorldObject.UCKELEOID ? 1 : 0;
			_inputs[3] = facingObject == WorldObject.WALL ? 1 : 0;
			_inputs[4] = ((double)this._food) / MAXIMUM_FOOD;
			// Read memory units
			for(int i = 0; i < this._memoryUnits.length; i++)
			{
				_inputs[Uckeleoid.BRAIN_INPUTS - 1 + i] = _memoryUnits[i];
			}
			// Read sound inputs
			for(int i = 0; i < this._soundInputs.length; i++)
			{
				_inputs[Uckeleoid.BRAIN_INPUTS - 1 + this._memoryUnits.length + i] = _soundInputs[i];
			}
			// Main brain computation
			double[] outputs = this._brain.outputs(_inputs);
			// Save memory units
			for(int i = 0; i < this._memoryUnits.length; i++)
			{
				_memoryUnits[i] = outputs[Uckeleoid.BRAIN_OUTPUTS + i - 1];
			}
			// Clear the sound inputs and set the sound outputs
			for(int i = 0; i < this._soundInputs.length; i++)
			{
				this._soundInputs[i] = 0;
				this._soundOutputs[i] = outputs[Uckeleoid.BRAIN_OUTPUTS - 1 + this._memoryUnits.length + i];
			}
			// Hard coded outputs (actionable outputs)
			int maxActionOutput = 0;
			for(int i = 1; i < Uckeleoid.BRAIN_OUTPUTS; i++)
			{
				if(outputs[i] > outputs[maxActionOutput])
				{
					maxActionOutput = i;
				}
			}
			// Return the output conversion to action
			return(UckeleoidAction.convertIntegerToAction(maxActionOutput));
		}
	}

	public UckeleoidAction getInvoluntaryAction()
	{
		// Forced actions
		// Old Uckeleoids Die
		if(this._age > Uckeleoid.MAXIMUM_AGE || this._food < 1)
		{
			return(UckeleoidAction.DIE);
		}
		// Pregnant Uckeleoids can give birth
		else if(this._gestation > Uckeleoid.MAXIMUM_GESTATION)
		{
			return(UckeleoidAction.BIRTH);
		}

		// If there are no involuntary actions, return null
		return(null);
	}

	public Direction getFacing()
	{
		return(this._facing);
	}

	public double getRandomSeed()
	{
		return(this._randomSeed);
	}

	public void setPosition(int r, int c)
	{
		this._r = r;
		this._c = c;
	}

	public int getR()
	{
		return(this._r);
	}

	public int getC()
	{
		return(this._c);
	}

	public void setIsFemale(boolean isFemale)
	{
		this._gender = isFemale ? Gender.FEMALE : Gender.MALE;
	}

	public boolean getIsFemale()
	{
		return(this._gender == Gender.FEMALE);
	}

	public Uckeleoid getFetus()
	{
		return(_fetus);
	}

	public UckeleoidBrain getBrain()
	{
		return(_brain);
	}

	public void executeAction(UckeleoidAction action)
	{
		executeAction(action, null);
	}

	public void executeAction(UckeleoidAction action, Uckeleoid breedingTarget)
	{
		switch(action)
		{
			case BIRTH:
				this._gestation = 0;
				break;
			case BREED:
				assert breedingTarget != null;
				if(this._gender == Gender.FEMALE && this._gestation < 1)
				{
					this._gestation = 1;
					this._fetus = createOffspringWith(breedingTarget);
				}
				this._food += Uckeleoid.BREEDING_FOOD_RATE;
				break;
			case MOVE:
				this._food += Uckeleoid.MOVING_FOOD_RATE;
				this._r += Direction.getVerticalComponent(this._facing);
				this._c += Direction.getHorizontalComponent(this._facing);
				break;
			case REST:
			case DIE:
				// change nothing
				break;
			case TURN_LEFT:
				this._facing = Direction.stepCounterclockwise(this._facing);
				break;
			case TURN_RIGHT:
				this._facing = Direction.stepClockwise(this._facing);
				break;
			case EAT:
				this._food += Uckeleoid.EATING_FOOD_RATE;
				if(this._food > Uckeleoid.MAXIMUM_FOOD)
				{
					this._food = Uckeleoid.MAXIMUM_FOOD;
				}
				break;
			default:
				System.err.println("Non-Fatal Error, unhandled action");
				new Error().printStackTrace();
				break;
		}
		if(this._world.getWorldVariables().getKeepGenerationActionProfile())
		{
			this._actionProfile.recordAction(action, true);
			this._generationGenderActionProfile.recordAction(action, true);
		}

	}

	public void failAction(UckeleoidAction action)
	{
		switch(action)
		{
			case BIRTH:
				// If gestation has been reached but the birth failed,
				// let the Uckeleoid have a round of freedom before trying to
				// give
				// birth again
				this._gestation -= 2;
				break;
			case BREED:
				// Trying to breed for no good reason is still tiring
				this._food -= 10;
				break;
			case EAT:
				this._food -= 1;
				break;
			case MOVE:
				this._food -= 1;
				break;
			case DIE:
				break;
			case REST:
			case TURN_LEFT:
			case TURN_RIGHT:
				System.err.println("Action class "+action+" should not fail");
				break;
			default:
				System.err.println("Non-Fatal Error, unhandled action");
				new Error().printStackTrace();
				break;
		}
		if(this._world.getWorldVariables().getKeepGenerationActionProfile())
		{
			this._actionProfile.recordAction(action, false);
			this._generationGenderActionProfile.recordAction(action, false);
		}
	}

	private Uckeleoid createOffspringWith(Uckeleoid breedingTarget)
	{
		return new Uckeleoid(this, breedingTarget, _world);
	}

	public String toString()
	{
		StringBuilder output = new StringBuilder();
		output.append("U_");
		output.append(this._id);
		output.append(" @(");
		output.append(this._r);
		output.append(',');
		output.append(this._c);
		output.append(") ");
		output.append("GEN-");
		output.append(this._generation);
		if(this._gender == Gender.FEMALE)
		{
			output.append('女');
		}
		else
		{
			output.append('男');
		}
		output.append("/a:");
		output.append(this._age);
		output.append("/g:");
		output.append(this._gestation);
		output.append("/f:");
		output.append(this._food);
		output.append("/f:");
		switch(this._facing)
		{
			case EAST:
				output.append('东');
				break;
			case NORTH:
				output.append('北');
				break;
			case SOUTH:
				output.append('南');
				break;
			case WEST:
				output.append('西');
				break;
		}

		output.append(' ');

		for(int i = 0; i < this._memoryUnits.length; i++)
		{
			output.append(this._memoryUnits[i]);
		}

		output.append(' ');

		output.append(this._action);

		return(output.toString());
	}

	public int getID()
	{
		return(this._id);
	}

	public double getGeneration()
	{
		return(this._generation);
	}

	public int getAge()
	{
		return(this._age);
	}

	public int getGestation()
	{
		return(this._gestation);
	}

	public int getFood()
	{
		return(this._food);
	}

	public void setGeneration(double generation)
	{
		this._generation = generation;
	}

	public void setRandomSeed(double randomSeed)
	{
		this._randomSeed = randomSeed;
	}

	public void setAge(int age)
	{
		this._age = age;
	}

	public void setGestation(int gestation)
	{
		this._gestation = gestation;
	}

	public void setFood(int food)
	{
		this._food = food;
	}

	public void setFacing(Direction facing)
	{
		this._facing = facing;
	}

	public void setFetus(Uckeleoid fetus)
	{
		this._fetus = fetus;
	}

	public void setBrain(UckeleoidBrain brain)
	{
		this._brain = brain;
	}

	public ActionProfile getActionHistory()
	{
		if(this._world.getWorldVariables().getKeepGenerationActionProfile())
		{
			return _actionProfile;
		}
		else
		{
			throw new Error("Action Profiles are not supported in this world");
		}
	}

	public void setActionHistory(ActionProfile _actionHistory)
	{
		this._actionProfile = _actionHistory;
	}
}
