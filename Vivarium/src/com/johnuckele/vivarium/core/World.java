package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.LinkedList;

import com.johnuckele.vivarium.visualization.RenderCode;

public class World implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long		serialVersionUID					= 3L;

	/**
	 * Constants
	 */
	private static final double		DEFAULT_FOOD_GENERATION_THRESHOLD	= 0.01;
	private static final double		DEFAULT_FOOD_THRESHOLD				= 0.2;
	private static final double		DEFAULT_UCKELEOID_THRESHOLD			= 0.2;
	private static final double		DEFAULT_WALL_THRESHOLD				= 0.1;

	private int						_maximumUckeleoidID;
	protected int					_worldDimensions;
	private double					_foodGenerationThreshold;

	protected WorldObject[][]		_world;
	protected Uckeleoid[][]			_uckeleoidGrid;
	protected LinkedList<Uckeleoid>	_uckeleoidList;
	private LinkedList<Uckeleoid>	_deadUckeleoidList;

	public World(int worldDimensions)
	{
		this._maximumUckeleoidID = 0;
		this._foodGenerationThreshold = World.DEFAULT_FOOD_GENERATION_THRESHOLD;

		this.setWorldDimensions(worldDimensions, true);
	}

	public void setObject(WorldObject obj, int r, int c)
	{
		if( obj == WorldObject.UCKELEOID )
		{
			throw new Error("Uckeleoid WorldObjects should not be assinged directly, use setUckeleoid");
		}
		setObject(obj, null, r, c);
	}

	private void setUckeleoid(Uckeleoid newUckeleoid, int r, int c)
	{
		setObject(WorldObject.UCKELEOID, newUckeleoid, r, c);
	}

	public void addUckeleoidAtPosition(Uckeleoid newUckeleoid, int r, int c)
	{
		setObject(WorldObject.UCKELEOID, newUckeleoid, r, c);
		this._uckeleoidList.add(newUckeleoid);
	}

	private void setObject(WorldObject obj, Uckeleoid newUckeleoid, int r, int c)
	{
		_world[r][c] = obj;
		if(obj == WorldObject.UCKELEOID)
		{
			newUckeleoid.setPosition(r, c);
			_uckeleoidGrid[r][c] = newUckeleoid;
		}
		else
		{
			_uckeleoidGrid[r][c] = null;
		}
	}

	public int requestNewUckleoidID()
	{
		return(++_maximumUckeleoidID);
	}

	/**
	 * Top level simulation step of the entire world and all denizens within it.
	 * Simulations are divided into four phases: 1, each uckeleoid will age and
	 * compute other time based values. 2, each uckeleoid will decide on an
	 * action to attempt. 3, each uckeleoid will attempt to execute the planned
	 * action (the order of execution on the actions is currently left to right,
	 * top to bottom, so some uckeleoids will get priority if actions conflict).
	 * 4, finally, food is spawned at a constant chance in empty spaces in the
	 * world.
	 */
	public void tick()
	{
		// Each uckeleoid calculates time based
		// changes in condition such as age,
		// gestation, and energy levels.
		tickUckeleoids();

		// Each uckeleoid plans which actions to
		// attempt to do during the next phase
		letUckeleoidsPlan();

		// Each uckeleoid will physically try to carry
		// out the planned action
		executeUckeleoidActions();

		// New food resources will be spawned in the world
		spawnFood();
	}

	private void tickUckeleoids()
	{
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(_world[r][c] == WorldObject.UCKELEOID)
				{
					_uckeleoidGrid[r][c].tick();
				}
			}
		}
	}

	private void letUckeleoidsPlan()
	{
		for(Uckeleoid uckeleoid : this._uckeleoidList)
		{
			uckeleoid.planAction();
		}
	}

	private void executeUckeleoidActions()
	{
		// Uckeleoids act
		LinkedList<Uckeleoid> dyingUckeleoids = new LinkedList<Uckeleoid>();
		LinkedList<Uckeleoid> spawningUckeleoids = new LinkedList<Uckeleoid>();
		for(Uckeleoid uckeleoid : this._uckeleoidList)
		{
			UckeleoidAction uckeleoidAction = uckeleoid.getAction();
			Direction uckeleoidFacing = uckeleoid.getFacing();
			int r = uckeleoid.getR();
			int c = uckeleoid.getC();
			int facingR = r + Direction.getVerticalComponent(uckeleoidFacing);
			int facingC = c + Direction.getHorizontalComponent(uckeleoidFacing);
			// Death
			if(uckeleoidAction == UckeleoidAction.DIE)
			{
				removeObject(r, c);
				dyingUckeleoids.add(uckeleoid);
			}
			// Various actions that always succeed and are simple
			else if(uckeleoidAction == UckeleoidAction.TURN_LEFT || uckeleoidAction == UckeleoidAction.TURN_RIGHT)
			{
				uckeleoid.executeAction(uckeleoidAction);
			}
			// Movement
			else if(uckeleoidAction == UckeleoidAction.MOVE && _world[facingR][facingC] == WorldObject.EMPTY)
			{
				uckeleoid.executeAction(uckeleoidAction);
				moveObject(r, c, uckeleoidFacing);
			}
			// Eating
			else if(uckeleoidAction == UckeleoidAction.EAT && _world[facingR][facingC] == WorldObject.FOOD)
			{
				uckeleoid.eat();
				removeObject(r, c, uckeleoidFacing);
			}
			// Attempt to breed
			else if(uckeleoidAction == UckeleoidAction.BREED
			// Make sure we're facing another uckeleoid
					&& _world[facingR][facingC] == WorldObject.UCKELEOID
					// And that uckeleoid also is trying to breed
					&& _uckeleoidGrid[facingR][facingC].getAction() == UckeleoidAction.BREED
					// Make sure the uckeleoids are facing each other
					&& uckeleoid.getFacing() == Direction.flipDirection(_uckeleoidGrid[facingR][facingC].getFacing()))
			{
				uckeleoid.executeAction(uckeleoidAction, _uckeleoidGrid[facingR][facingC]);
			}
			// Giving Birth
			else if(uckeleoidAction == UckeleoidAction.BIRTH && _world[facingR][facingC] == WorldObject.EMPTY)
			{
				Uckeleoid spawningUckeleoid = uckeleoid.getFetus();
				uckeleoid.executeAction(uckeleoidAction);
				setUckeleoid(spawningUckeleoid, facingR, facingC);
				spawningUckeleoids.add(spawningUckeleoid);
			}
			// Action failed
			else
			{
				uckeleoid.failAction(uckeleoidAction);
			}
		}
		// Remove dead uckeleoids
		for(Uckeleoid deadUckeleoid : dyingUckeleoids)
		{
			this._uckeleoidList.remove(deadUckeleoid);
			this._deadUckeleoidList.add(deadUckeleoid);
		}
		// Add newborn rats
		for(Uckeleoid spawningUckeleoid : spawningUckeleoids)
		{
			this._uckeleoidList.add(spawningUckeleoid);
		}
	}

	private void spawnFood()
	{
		// Generate food at a given rate
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(_world[r][c] == WorldObject.EMPTY)
				{
					double randomNumber = Math.random();
					if(randomNumber < this._foodGenerationThreshold)
					{
						setObject(WorldObject.FOOD, r, c);
						_world[r][c] = WorldObject.FOOD;
					}
				}
			}
		}
	}

	public int getWorldDimensions()
	{
		return this._worldDimensions;
	}

	public WorldObject getWorldObject(int r, int c)
	{
		return(this._world[r][c]);
	}

	private void moveObject(int r, int c, Direction direction)
	{
		int r1 = r;
		int c1 = c;
		int r2 = r;
		int c2 = c;
		switch(direction)
		{
			case NORTH:
				r2--;
				break;
			case EAST:
				c2++;
				break;
			case SOUTH:
				r2++;
				break;
			case WEST:
				c2--;
				break;
			default:
				System.err.println("Non-Fatal Error, unhandled action");
				new Error().printStackTrace();
		}

		// Default object move
		_world[r2][c2] = _world[r1][c1];
		_world[r1][c1] = WorldObject.EMPTY;
		// Special uckeleoid move extras
		if(_world[r2][c2] == WorldObject.UCKELEOID)
		{
			_uckeleoidGrid[r2][c2] = _uckeleoidGrid[r1][c1];
			_uckeleoidGrid[r1][c1] = null;
		}
	}

	private void removeObject(int r, int c)
	{
		this.removeObject(r, c, Direction.NORTH, 0);
	}

	private void removeObject(int r, int c, Direction direction)
	{
		this.removeObject(r, c, direction, 1);
	}

	private void removeObject(int r, int c, Direction direction, int distance)
	{
		switch(direction)
		{
			case NORTH:
				r -= distance;
				break;
			case EAST:
				c += distance;
				break;
			case SOUTH:
				r += distance;
				break;
			case WEST:
				c -= distance;
				break;
			default:
				System.err.println("Non-Fatal Error, unhandled action");
				new Error().printStackTrace();
		}

		_world[r][c] = WorldObject.EMPTY;
		_uckeleoidGrid[r][c] = null;
	}

	public int getCount(WorldObject obj)
	{
		int count = 0;
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(this._world[r][c] == obj)
				{
					count++;
				}
			}
		}
		return(count);
	}

	public String toString(RenderCode code)
	{
		if(code == RenderCode.MAP)
		{
			// Draw world map
			StringBuilder worldOutput = new StringBuilder();
			worldOutput.append("Walls: ");
			worldOutput.append(this.getCount(WorldObject.WALL));
			worldOutput.append(", Uckeleoids: ");
			worldOutput.append(this.getCount(WorldObject.UCKELEOID));
			worldOutput.append(", Food: ");
			worldOutput.append(this.getCount(WorldObject.FOOD));
			worldOutput.append('\n');
			for(int r = 0; r < _worldDimensions; r++)
			{
				for(int c = 0; c < _worldDimensions; c++)
				{
					if(_world[r][c] == WorldObject.EMPTY)
					{
						worldOutput.append('\u3000');
					}
					else if(_world[r][c] == WorldObject.WALL)
					{
						worldOutput.append('口');
					}
					else if(_world[r][c] == WorldObject.FOOD)
					{
						worldOutput.append('一');
					}
					else if(_world[r][c] == WorldObject.UCKELEOID)
					{
						worldOutput.append('中');
					}
				}
				worldOutput.append('\n');
			}
			return(worldOutput.toString());
		}
		else if(code == RenderCode.BRAIN_WEIGHTS)
		{
			// Draw average brain
			// Draw uckeleoid readouts
			StringBuilder uckeleoidBrainOutput = new StringBuilder();
			LinkedList<UckeleoidBrain> brains = new LinkedList<UckeleoidBrain>();
			for(Uckeleoid rat : this._uckeleoidList)
			{
				brains.add(rat.getBrain());
			}
			if(brains.size() > 0)
			{
				UckeleoidBrain minBrain = UckeleoidBrain.minBrain(brains);
				UckeleoidBrain maxBrain = UckeleoidBrain.maxBrain(brains);
				UckeleoidBrain medianBrain = UckeleoidBrain.medianBrain(brains);
				UckeleoidBrain standardDeviationBrain = UckeleoidBrain.standardDeviationBrain(brains, medianBrain);
				uckeleoidBrainOutput.append("Average uckeleoid NN:\n");
				uckeleoidBrainOutput.append(medianBrain.toString());
				uckeleoidBrainOutput.append("Std. Deviation on uckeleoid NNs:\n");
				uckeleoidBrainOutput.append(standardDeviationBrain.toString());
				uckeleoidBrainOutput.append("Min uckeleoid NN:\n");
				uckeleoidBrainOutput.append(minBrain.toString());
				uckeleoidBrainOutput.append("Max uckeleoid NN:\n");
				uckeleoidBrainOutput.append(maxBrain.toString());
			}
			return(uckeleoidBrainOutput.toString());
		}
		else if(code == RenderCode.UCKELEOID_LIST)
		{
			StringBuilder uckeleoidOutput = new StringBuilder();
			for(Uckeleoid uckeleoid : this._uckeleoidList)
			{
				uckeleoidOutput.append(uckeleoid.toString());
				uckeleoidOutput.append('\n');
			}
			return(uckeleoidOutput.toString());
		}
		else if(code == RenderCode.DEAD_UCKELEOID_LIST)
		{
			StringBuilder uckeleoidOutput = new StringBuilder();
			for(Uckeleoid uckeleoid : this._deadUckeleoidList)
			{
				uckeleoidOutput.append(uckeleoid.toString());
				uckeleoidOutput.append('\n');
			}
			return(uckeleoidOutput.toString());
		}
		else
		{
			throw new Error("Invalid Code");
		}
	}

	public int getMaximumUckeleoidID()
	{
		return this._maximumUckeleoidID;
	}

	public double getFoodGenerationThreshold()
	{
		return this._foodGenerationThreshold;
	}

	public void setMaximumUckeleoidID(int maximumUckeleoidID)
	{
		this._maximumUckeleoidID = maximumUckeleoidID;
	}

	public void setFoodGenerationThreshold(double foodGenerationThreshold)
	{
		this._foodGenerationThreshold = foodGenerationThreshold;
	}

	public void setWorldDimensions(int worldDimensions, boolean automaticallyPopulateWorld)
	{
		this._worldDimensions = worldDimensions;

		this._world = new WorldObject[_worldDimensions][_worldDimensions];
		this._uckeleoidGrid = new Uckeleoid[_worldDimensions][_worldDimensions];
		this._uckeleoidList = new LinkedList<Uckeleoid>();
		this._deadUckeleoidList = new LinkedList<Uckeleoid>();

		if(automaticallyPopulateWorld)
		{
			for(int r = 0; r < worldDimensions; r++)
			{
				for(int c = 0; c < worldDimensions; c++)
				{
					_world[r][c] = WorldObject.EMPTY;
					_uckeleoidGrid[r][c] = null;
					if(r < 1 || c < 1 || r > _worldDimensions - 2 || c > _worldDimensions - 2)
					{
						_world[r][c] = WorldObject.WALL;
					}
					else
					{
						double randomNumber = Math.random();
						if(randomNumber < DEFAULT_WALL_THRESHOLD)
						{
							_world[r][c] = WorldObject.WALL;
						}
						else if(randomNumber < DEFAULT_WALL_THRESHOLD + DEFAULT_FOOD_THRESHOLD)
						{
							_world[r][c] = WorldObject.FOOD;
						}
						else if(randomNumber < DEFAULT_WALL_THRESHOLD + DEFAULT_FOOD_THRESHOLD + DEFAULT_UCKELEOID_THRESHOLD)
						{
							_world[r][c] = WorldObject.UCKELEOID;
							Uckeleoid uckeleoid = new Uckeleoid(this, r, c);
							_uckeleoidGrid[r][c] = uckeleoid;
							_uckeleoidList.add(uckeleoid);
						}
					}
				}
			}
		}
	}

	public Uckeleoid getUckeleoid(int r, int c)
	{
		return this._uckeleoidGrid[r][c];
	}
}
