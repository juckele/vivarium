package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import com.johnuckele.vivarium.visualization.RenderCode;

public class World implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long		serialVersionUID					= 4L;
	public static Random RANDOM = new Random();

	private WorldVariables			_worldVariables;

	private int						_maximumUckeleoidID;
	private int						_tickCounter;
	protected int					_worldDimensions;

	protected WorldObject[][]		_worldObjectGrid;
	protected Uckeleoid[][]			_uckeleoidGrid;
	protected LinkedList<Uckeleoid>	_liveUckeleoidList;
	private LinkedList<Uckeleoid>	_deadUckeleoidList;
	private CensusRecord			_census;


	public World(int worldDimensions, WorldVariables worldVariables)
	{
		this._worldVariables = worldVariables;
		this._maximumUckeleoidID = 0;
		this._tickCounter = 0;

		this.setWorldDimensions(worldDimensions);
		this.populatateWorld();
		this._census = new CensusRecord(this.getCount(WorldObject.UCKELEOID));
	}

	public int getWorldDimensions()
	{
		return this._worldDimensions;
	}

	private void populatateWorld()
	{
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				_worldObjectGrid[r][c] = WorldObject.EMPTY;
				_uckeleoidGrid[r][c] = null;
				if(r < 1 || c < 1 || r > _worldDimensions - 2 || c > _worldDimensions - 2)
				{
					_worldObjectGrid[r][c] = WorldObject.WALL;
				}
				else
				{
					double randomNumber = Math.random();
					double wallProbability = _worldVariables.getInitialWallGenerationProbability();
					double foodProbability = _worldVariables.getInitialFoodGenerationProbability();
					double uckeleoidProbability = _worldVariables.getInitialUckeleoidGenerationProbability();
					if(randomNumber < wallProbability)
					{
						_worldObjectGrid[r][c] = WorldObject.WALL;
					}
					else if(randomNumber < wallProbability + foodProbability)
					{
						_worldObjectGrid[r][c] = WorldObject.FOOD;
					}
					else if(randomNumber < wallProbability + foodProbability + uckeleoidProbability)
					{
						_worldObjectGrid[r][c] = WorldObject.UCKELEOID;
						Uckeleoid uckeleoid = new Uckeleoid(this, r, c);
						_uckeleoidGrid[r][c] = uckeleoid;
						_liveUckeleoidList.add(uckeleoid);
					}
				}
			}
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

		// Keep the census up to date
		this._tickCounter++;
		this._census.updateRecords(this._tickCounter, this._liveUckeleoidList.size());
	}

	private void tickUckeleoids()
	{
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(_worldObjectGrid[r][c] == WorldObject.UCKELEOID)
				{
					_uckeleoidGrid[r][c].tick();
				}
			}
		}
	}

	private void letUckeleoidsPlan()
	{
		for(Uckeleoid uckeleoid : this._liveUckeleoidList)
		{
			uckeleoid.planAction();
		}
	}

	private void executeUckeleoidActions()
	{
		// Uckeleoids act
		LinkedList<Uckeleoid> dyingUckeleoids = new LinkedList<Uckeleoid>();
		LinkedList<Uckeleoid> spawningUckeleoids = new LinkedList<Uckeleoid>();
		for(Uckeleoid uckeleoid : this._liveUckeleoidList)
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
				uckeleoid.executeAction(uckeleoidAction);
				removeObject(r, c);
				dyingUckeleoids.add(uckeleoid);
			}
			// Various actions that always succeed and are simple
			else if(uckeleoidAction == UckeleoidAction.TURN_LEFT || uckeleoidAction == UckeleoidAction.TURN_RIGHT || uckeleoidAction == UckeleoidAction.REST)
			{
				uckeleoid.executeAction(uckeleoidAction);
			}
			// Movement
			else if(uckeleoidAction == UckeleoidAction.MOVE && _worldObjectGrid[facingR][facingC] == WorldObject.EMPTY)
			{
				uckeleoid.executeAction(uckeleoidAction);
				moveObject(r, c, uckeleoidFacing);
			}
			// Eating
			else if(uckeleoidAction == UckeleoidAction.EAT && _worldObjectGrid[facingR][facingC] == WorldObject.FOOD)
			{
				uckeleoid.executeAction(uckeleoidAction);
				removeObject(r, c, uckeleoidFacing);
			}
			// Attempt to breed
			else if(uckeleoidAction == UckeleoidAction.BREED
			// Make sure we're facing another uckeleoid
					&& _worldObjectGrid[facingR][facingC] == WorldObject.UCKELEOID
					// And that uckeleoid also is trying to breed
					&& _uckeleoidGrid[facingR][facingC].getAction() == UckeleoidAction.BREED
					// Make sure the uckeleoids are facing each other
					&& uckeleoid.getFacing() == Direction.flipDirection(_uckeleoidGrid[facingR][facingC].getFacing()))
			{
				uckeleoid.executeAction(uckeleoidAction, _uckeleoidGrid[facingR][facingC]);
			}
			// Giving Birth
			else if(uckeleoidAction == UckeleoidAction.BIRTH && _worldObjectGrid[facingR][facingC] == WorldObject.EMPTY)
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
			this._liveUckeleoidList.remove(deadUckeleoid);
			if(this._worldVariables.getRememberTheDead())
			{
				this._deadUckeleoidList.add(deadUckeleoid);
			}
		}
		// Add newborn rats
		for(Uckeleoid spawningUckeleoid : spawningUckeleoids)
		{
			this._liveUckeleoidList.add(spawningUckeleoid);
		}
	}

	private void spawnFood()
	{
		// Generate food at a given rate
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(_worldObjectGrid[r][c] == WorldObject.EMPTY)
				{
					double randomNumber = Math.random();
					if(randomNumber < this._worldVariables.getFoodGenerationProbability())
					{
						setObject(WorldObject.FOOD, r, c);
						_worldObjectGrid[r][c] = WorldObject.FOOD;
					}
				}
			}
		}
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
		_worldObjectGrid[r2][c2] = _worldObjectGrid[r1][c1];
		_worldObjectGrid[r1][c1] = WorldObject.EMPTY;
		// Special uckeleoid move extras
		if(_worldObjectGrid[r2][c2] == WorldObject.UCKELEOID)
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

		_worldObjectGrid[r][c] = WorldObject.EMPTY;
		_uckeleoidGrid[r][c] = null;
	}

	public LinkedList<Uckeleoid> getAllUckeleoids()
	{
		LinkedList<Uckeleoid> allUckeleoids = new LinkedList<Uckeleoid>();
		allUckeleoids.addAll(this._liveUckeleoidList);
		allUckeleoids.addAll(this._deadUckeleoidList);
		Collections.sort(allUckeleoids, new Comparator<Uckeleoid>()
		{
			@Override public int compare(Uckeleoid o1, Uckeleoid o2)
			{
				if(o1.getGeneration() > o2.getGeneration())
				{
					return 1;
				}
				else if(o1.getGeneration() < o2.getGeneration())
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		});
		return allUckeleoids;
	}

	public CensusRecord getCensus()
	{
		return this._census;
	}

	public int getCount(WorldObject obj)
	{
		int count = 0;
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(this._worldObjectGrid[r][c] == obj)
				{
					count++;
				}
			}
		}
		return(count);
	}

	public int getMaximumUckeleoidID()
	{
		return this._maximumUckeleoidID;
	}

	public void setMaximumUckeleoidID(int maximumUckeleoidID)
	{
		this._maximumUckeleoidID = maximumUckeleoidID;
	}

	public int getTickCounter()
	{
		return this._tickCounter;
	}

	public void setTickCounter(int tickCounter)
	{
		this._tickCounter = tickCounter;
	}

	public Uckeleoid getUckeleoid(int r, int c)
	{
		return this._uckeleoidGrid[r][c];
	}

	public void addUckeleoidAtPosition(Uckeleoid newUckeleoid, int r, int c)
	{
		setObject(WorldObject.UCKELEOID, newUckeleoid, r, c);
		this._liveUckeleoidList.add(newUckeleoid);
	}

	private void setUckeleoid(Uckeleoid newUckeleoid, int r, int c)
	{
		setObject(WorldObject.UCKELEOID, newUckeleoid, r, c);
	}

	public void setWorldDimensions(int worldDimensions)
	{
		this._worldDimensions = worldDimensions;

		this._worldObjectGrid = new WorldObject[_worldDimensions][_worldDimensions];
		this._uckeleoidGrid = new Uckeleoid[_worldDimensions][_worldDimensions];
		this._liveUckeleoidList = new LinkedList<Uckeleoid>();
		this._deadUckeleoidList = new LinkedList<Uckeleoid>();
	}

	public WorldObject getWorldObject(int r, int c)
	{
		return(this._worldObjectGrid[r][c]);
	}

	public void setObject(WorldObject obj, int r, int c)
	{
		if(obj == WorldObject.UCKELEOID)
		{
			throw new Error("Uckeleoid WorldObjects should not be assinged directly, use setUckeleoid");
		}
		setObject(obj, null, r, c);
	}

	public WorldVariables getWorldVariables()
	{
		return _worldVariables;
	}

	private void setObject(WorldObject obj, Uckeleoid newUckeleoid, int r, int c)
	{
		_worldObjectGrid[r][c] = obj;
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

	public String toString(RenderCode code)
	{
		if(code == RenderCode.MAP)
		{
			return(renderMap());
		}
		else if(code == RenderCode.BRAIN_WEIGHTS)
		{
			return(renderBrainWeights());
		}
		else if(code == RenderCode.UCKELEOID_LIST)
		{
			StringBuilder uckeleoidOutput = new StringBuilder();
			for(Uckeleoid uckeleoid : this.getAllUckeleoids())
			{
				uckeleoidOutput.append(uckeleoid.toString());
				uckeleoidOutput.append('\n');
			}
			return(uckeleoidOutput.toString());
		}
		else if(code == RenderCode.LIVE_UCKELEOID_LIST)
		{
			StringBuilder uckeleoidOutput = new StringBuilder();
			for(Uckeleoid uckeleoid : this._liveUckeleoidList)
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

	private String renderMap()
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
				if(_worldObjectGrid[r][c] == WorldObject.EMPTY)
				{
					worldOutput.append('\u3000');
				}
				else if(_worldObjectGrid[r][c] == WorldObject.WALL)
				{
					worldOutput.append('口');
				}
				else if(_worldObjectGrid[r][c] == WorldObject.FOOD)
				{
					worldOutput.append('一');
				}
				else if(_worldObjectGrid[r][c] == WorldObject.UCKELEOID)
				{
					worldOutput.append('中');
				}
			}
			worldOutput.append('\n');
		}
		return(worldOutput.toString());
	}

	private String renderBrainWeights()
	{
		// Draw average brain
		// Draw uckeleoid readouts
		StringBuilder uckeleoidBrainOutput = new StringBuilder();
		LinkedList<UckeleoidBrain> brains = new LinkedList<UckeleoidBrain>();
		for(Uckeleoid rat : this._liveUckeleoidList)
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
}
