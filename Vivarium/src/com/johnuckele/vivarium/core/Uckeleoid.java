package com.johnuckele.vivarium.core;

import java.io.Serializable;

import com.johnuckele.vivarium.UtilityFunctions;

public class Uckeleoid implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long	serialVersionUID	= 2L;

	/**
	 * Constants
	 */
	private static final double	DEFAULT_FEMALE_THRESHOLD	= 0.6;
	private static final int	MAXIMUM_AGE					= 20000;
	private static final int	MAXIMUM_GESTATION			= 2000;
	private static final int	MAXIMUM_FOOD				= 2000;

	private static final int	BREEDING_FOOD_RATE			= -10;
	private static final int	EATING_FOOD_RATE			= 500;
	private static final int	MOVING_FOOD_RATE			= -1;
	private static final int	BASE_FOOD_RATE				= -1;
	private static final int	PREGNANT_FOOD_RATE			= -2;
	private static final int	BRAIN_INPUTS				= 4;
	private static final int	BRAIN_OUTPUTS				= 6;
	private static final int	MEMORY_UNITS				= 0;

	/**
	 * individual specific members
	 */
	private World				_world;
	private int					_r;
	private int					_c;
	private double				_generation;

	private UckeleoidBrain		_brain;
	private double[]			_inputs;
	private double[]			_memoryUnits;

	private boolean				_isFemale;
	private double				_foodPreferences1;
	private double				_foodPreferences2;
	private int					_id;
	private int					_age;
	private int					_gestation;
	private int					_food;
	private Direction			_facing;
	private UckeleoidAction		_action;

	private Uckeleoid			_fetus;

	// Randomly initialized Uckeleoid
	public Uckeleoid(World world, int r, int c)
	{
		this(null, null, world, r, c, Uckeleoid.DEFAULT_FEMALE_THRESHOLD);
	}

	public Uckeleoid(Uckeleoid parent1, Uckeleoid parent2, World world)
	{
		this(parent1, parent2, world, 0, 0, Uckeleoid.DEFAULT_FEMALE_THRESHOLD);
	}

	public Uckeleoid(Uckeleoid parent1, Uckeleoid parent2, World world, int r, int c, double femaleThreshold)
	{
		this._id = world.requestNewUckleoidID();

		this._world = world;
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
		if(parent1 != null)
		{
			if(parent2 != null)
			{
				// Brain combined from genetic legacy
				this._brain = new UckeleoidBrain(parent1._brain, parent2._brain);
			}
			else
			{
				throw new Error("Uckeleoid with single parent");
			}
		}
		else if(parent2 != null)
		{
			throw new Error("Uckeleoid with single parent");
		}
		else
		{
			// Create a new default brain
			this._brain = new UckeleoidBrain(Uckeleoid.BRAIN_INPUTS + Uckeleoid.MEMORY_UNITS, Uckeleoid.BRAIN_OUTPUTS + Uckeleoid.MEMORY_UNITS, 0);
		}
		_inputs = new double[Uckeleoid.BRAIN_INPUTS + Uckeleoid.MEMORY_UNITS];
		_memoryUnits = new double[Uckeleoid.MEMORY_UNITS];

		// Set gender
		double randomNumber = Math.random();
		if(randomNumber < femaleThreshold)
		{
			this._isFemale = true;
		}
		else
		{
			this._isFemale = false;
		}

		// Set food trait preferences
		double randomAngle = Math.random() * 2 * Math.PI;
		this._foodPreferences1 = Math.sin(randomAngle);
		this._foodPreferences2 = Math.cos(randomAngle);

		// Set defaults
		this._age = 0;
		this._gestation = 0;
		this._food = Uckeleoid.MAXIMUM_FOOD;
		this._facing = Direction.NORTH;
		this._action = UckeleoidAction.REST;
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
			WorldObject facingObject = this._world.getObject(facingR, facingC);
			_inputs[0] = this._isFemale ? 1 : 0;
			_inputs[1] = facingObject == WorldObject.FOOD ? 1 : 0;
			_inputs[2] = facingObject == WorldObject.UCKELEOID ? 1 : 0;
			_inputs[3] = facingObject == WorldObject.WALL ? 1 : 0;
			// Read memory units
			for(int i = 0; i < Uckeleoid.MEMORY_UNITS; i++)
			{
				_memoryUnits[i] = _inputs[Uckeleoid.BRAIN_INPUTS + i];
			}
			// Main computation
			double[] outputs = this._brain.outputs(_inputs);
			// Save memory units
			for(int i = 0; i < Uckeleoid.MEMORY_UNITS; i++)
			{
				_memoryUnits[i] = outputs[Uckeleoid.BRAIN_OUTPUTS + i];
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
		// Pregnant Rats can give birth
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
				if(this._isFemale && this._gestation < 1)
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
				// change nothing
				break;
			case TURN_LEFT:
				this._facing = Direction.stepCounterclockwise(this._facing);
				break;
			case TURN_RIGHT:
				this._facing = Direction.stepClockwise(this._facing);
				break;
			case EAT:
				// Eat is now handled by the dedicated eat function because
				// it requires specific information to be passed, generic
				// action handling on eat should not be invoked
			case DIE:
			default:
				System.err.println("Non-Fatal Error, unhandled action");
				new Error().printStackTrace();
				break;
		}
	}

	public void eat(double trait1, double trait2)
	{
		// double traitCrossProduct = this._foodPreferences1 * trait1 +
		// this._foodPreferences2 * trait2;
		this._food += Uckeleoid.EATING_FOOD_RATE;
		// this._food += (0.5 + traitCrossProduct) * Rat.EATING_FOOD_RATE;
		if(this._food > Uckeleoid.MAXIMUM_FOOD)
		{
			this._food = Uckeleoid.MAXIMUM_FOOD;
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
			case REST:
			case TURN_LEFT:
			case TURN_RIGHT:
			case DIE:
			default:
				break;
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
		output.append(String.format("%04d", this._id));
		output.append(" @(");
		output.append(String.format("%02d", this._r));
		output.append(',');
		output.append(String.format("%02d", this._c));
		output.append(") ");
		output.append("GEN-");
		output.append(String.format("%1$.1f ", this._generation));
		if(this._isFemale)
		{
			output.append('女');
		}
		else
		{
			output.append('男');
		}
		output.append("/a:");
		output.append(String.format("%05d", this._age));
		output.append("/g:");
		output.append(String.format("%04d", this._gestation));
		output.append("/f:");
		output.append(String.format("%04d", this._food));
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

		for(int i = 0; i < Uckeleoid.MEMORY_UNITS; i++)
		{
			output.append(String.format("%1$.2f ", this._memoryUnits[i]));
		}
		output.append(String.format("%1$.2f ", this._foodPreferences1));
		output.append(String.format("%1$.2f ", this._foodPreferences2));

		output.append(' ');

		output.append(this._action);

		return(output.toString());
	}
}
