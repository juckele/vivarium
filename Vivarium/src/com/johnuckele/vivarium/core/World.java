package com.johnuckele.vivarium.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import com.johnuckele.vivarium.visualization.RenderCode;

public class World implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Constants
	 */
	private static final double	DEFAULT_WALL_THRESHOLD				= 0.1;
	private static final double	DEFAULT_RAT_THRESHOLD				= 0.2;
	private static final double	DEFAULT_FOOD_THRESHOLD				= 0.2;
	private static final double	DEFAULT_FOOD_GENERATION_THRESHOLD	= 0.01;

	private int					_worldDimensions;
	private WorldObject[][]		_world;
	private double[][][]		_foodProperties;
	private Uckeleoid[][]				_ratGrid;
	private LinkedList<Uckeleoid>		_ratList;
	private double				_foodGenerationThreshold;

	public World(File inputFile)
	{
		//TODO
	}

	public World(int worldDimensions)
	{
		this(worldDimensions, World.DEFAULT_WALL_THRESHOLD, World.DEFAULT_FOOD_THRESHOLD, World.DEFAULT_RAT_THRESHOLD);
	}

	public World(int worldDimensions, double wallThreshold, double foodThreshold, double ratThreshold)
	{
		this._worldDimensions = worldDimensions;
		this._world = new WorldObject[_worldDimensions][_worldDimensions];
		this._ratGrid = new Uckeleoid[_worldDimensions][_worldDimensions];
		this._foodProperties = new double[_worldDimensions][_worldDimensions][2];
		this._ratList = new LinkedList<Uckeleoid>();
		this._foodGenerationThreshold = World.DEFAULT_FOOD_GENERATION_THRESHOLD;

		for(int r = 0; r < worldDimensions; r++)
		{
			for(int c = 0; c < worldDimensions; c++)
			{
				_world[r][c] = WorldObject.EMPTY;
				_ratGrid[r][c] = null;
				if(r < 1 || c < 1 || r > _worldDimensions - 2 || c > _worldDimensions - 2)
				{
					_world[r][c] = WorldObject.WALL;
				}
				else
				{
					double randomNumber = Math.random();
					if(randomNumber < wallThreshold)
					{
						_world[r][c] = WorldObject.WALL;
					}
					else if(randomNumber < wallThreshold + foodThreshold)
					{
						_world[r][c] = WorldObject.FOOD;
						double randomAngle = Math.random() * 2 * Math.PI;
						this._foodProperties[r][c][0] = Math.sin(randomAngle);
						this._foodProperties[r][c][1] = Math.cos(randomAngle);
					}
					else if(randomNumber < wallThreshold + foodThreshold + ratThreshold)
					{
						_world[r][c] = WorldObject.UCKELEOID;
						Uckeleoid rat = new Uckeleoid(this, r, c);
						_ratGrid[r][c] = rat;
						_ratList.add(rat);
					}
				}
			}
		}
	}

	/**
	 * Top level simulation step of the entire world and all denizens within it.
	 * Simulations are divided into four phases: 1, each rat will age and
	 * compute other time based values. 2, each rat will decide on an action to
	 * attempt. 3, each rat will attempt to execute the planned action (the
	 * order of execution on the actions is currently left to right, top to
	 * bottom, so some rats will get priority if actions conflict). 4, finally,
	 * food is spawned at a constant chance in empty spaces in the world.
	 */
	public void tick()
	{
		// Each rat calculates time based
		// changes in condition such as age,
		// gestation, and energy levels.
		tickRats();

		// Each rat plans which actions to
		// attempt to do during the next phase
		letRatsPlan();

		// Each rat will physically try to carry
		// out the planned action
		executeRatActions();

		// New food resources will be spawned in the world
		spawnFood();
	}

	private void tickRats()
	{
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(_world[r][c] == WorldObject.UCKELEOID)
				{
					_ratGrid[r][c].tick();
				}
			}
		}
	}

	private void letRatsPlan()
	{
		for(Uckeleoid rat : this._ratList)
		{
			rat.planAction();
		}
	}

	private void executeRatActions()
	{
		// Rats act
		LinkedList<Uckeleoid> deadRats = new LinkedList<Uckeleoid>();
		LinkedList<Uckeleoid> babyRats = new LinkedList<Uckeleoid>();
		for(Uckeleoid rat : this._ratList)
		{
			UckeleoidAction ratAction = rat.getAction();
			Direction ratFacing = rat.getFacing();
			int r = rat.getR();
			int c = rat.getC();
			int facingR = r + Direction.getVerticalComponent(ratFacing);
			int facingC = c + Direction.getHorizontalComponent(ratFacing);
			// Death
			if(ratAction == UckeleoidAction.DIE)
			{
				removeObject(r, c);
				deadRats.add(rat);
			}
			// Various actions that always succeed and are simple
			else if(ratAction == UckeleoidAction.TURN_LEFT || ratAction == UckeleoidAction.TURN_RIGHT)
			{
				rat.executeAction(ratAction);
			}
			// Movement
			else if(ratAction == UckeleoidAction.MOVE && _world[facingR][facingC] == WorldObject.EMPTY)
			{
				rat.executeAction(ratAction);
				moveObject(r, c, ratFacing);
			}
			// Eating
			else if(ratAction == UckeleoidAction.EAT && _world[facingR][facingC] == WorldObject.FOOD)
			{
				rat.eat(_foodProperties[facingR][facingC][0], _foodProperties[facingR][facingC][1]);
				removeObject(r, c, ratFacing);
			}
			// Attempt to breed
			else if(ratAction == UckeleoidAction.BREED
			// Make sure we're facing another rat
					&& _world[facingR][facingC] == WorldObject.UCKELEOID
					// And that rat also is trying to breed
					&& _ratGrid[facingR][facingC].getAction() == UckeleoidAction.BREED
					// Make sure the rats are facing each other
					&& rat.getFacing() == Direction.flipDirection(_ratGrid[facingR][facingC].getFacing()))
			{
				rat.executeAction(ratAction, _ratGrid[facingR][facingC]);
			}
			// Giving Birth
			else if(ratAction == UckeleoidAction.BIRTH && _world[facingR][facingC] == WorldObject.EMPTY)
			{
				Uckeleoid babyRat = rat.getFetus();
				rat.executeAction(ratAction);
				createObject(babyRat, r, c, ratFacing);
				babyRats.add(babyRat);
			}
			// Action failed
			else
			{
				rat.failAction(ratAction);
			}
		}
		// Remove dead rats
		for(Uckeleoid deadRat : deadRats)
		{
			this._ratList.remove(deadRat);
		}
		// Add newborn rats
		for(Uckeleoid babyRat : babyRats)
		{
			this._ratList.add(babyRat);
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
						createObject(WorldObject.FOOD, r, c);
						_world[r][c] = WorldObject.FOOD;
					}
				}
			}
		}
	}

	public WorldObject getObject(int r, int c)
	{
		return(this._world[r][c]);
	}
	public double getFoodProperty(int r, int c, int property)
	{
		return(this._foodProperties[r][c][property]);
	}

	private void createObject(Uckeleoid babyRat, int r, int c, Direction direction)
	{
		this.createObject(WorldObject.UCKELEOID, babyRat, r, c, direction, 1);
	}

	private void createObject(WorldObject obj, int r, int c)
	{
		this.createObject(obj, null, r, c, Direction.NORTH, 0);
	}

	private void createObject(WorldObject obj, Uckeleoid newRat, int r, int c, Direction direction, int distance)
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

		_world[r][c] = obj;
		if(obj == WorldObject.UCKELEOID)
		{
			newRat.setPosition(r, c);
			_ratGrid[r][c] = newRat;
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
		_world[r2][c2] = _world[r1][c1];
		_world[r1][c1] = WorldObject.EMPTY;
		// Special rat move extras
		if(_world[r2][c2] == WorldObject.UCKELEOID)
		{
			_ratGrid[r2][c2] = _ratGrid[r1][c1];
			_ratGrid[r1][c1] = null;
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
		_ratGrid[r][c] = null;
		_foodProperties[r][c][0] = 0;
		_foodProperties[r][c][1] = 0;
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
		if(code == RenderCode.OVERVIEW)
		{
			// Draw world map
			StringBuilder worldOutput = new StringBuilder();
			worldOutput.append("Walls: ");
			worldOutput.append(this.getCount(WorldObject.WALL));
			worldOutput.append(", Rats: ");
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

			// Draw average brain
			// Draw rat readouts
			StringBuilder ratBrainOutput = new StringBuilder();
			LinkedList<UckeleoidBrain> brains = new LinkedList<UckeleoidBrain>();
			for(Uckeleoid rat : this._ratList)
			{
				brains.add(rat.getBrain());
			}
			if(brains.size() > 0)
			{
				UckeleoidBrain medianBrain = UckeleoidBrain.medianBrain(brains);
				UckeleoidBrain standardDeviationBrain = UckeleoidBrain.standardDeviationBrain(brains, medianBrain);
				ratBrainOutput.append("Average rat NN:\n");
				ratBrainOutput.append(medianBrain.toString());
				ratBrainOutput.append("Std. Deviation on rat NNs:\n");
				ratBrainOutput.append(standardDeviationBrain.toString());
			}
			return(worldOutput.toString() + ratBrainOutput.toString());
		}
		else if(code == RenderCode.RAT_LIST)
		{
			StringBuilder ratOutput = new StringBuilder();
			for(Uckeleoid rat : this._ratList)
			{
				ratOutput.append(rat.toString());
				ratOutput.append('\n');
			}
			return(ratOutput.toString());
		}
		else
		{
			throw new Error("Invalid Code");
		}
	}

	public static void main(String[] args)
	{
		File f = new File("test.viv");
		World w = new World(10);
		int uckeleoidCount = w.getCount(WorldObject.UCKELEOID);
		System.out.println("Uckeleoid count: "+uckeleoidCount);
		try
		{
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(w);
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		World w2 = null;
		try
		{
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			w2 = (World) ois.readObject();
			ois.close();
			fis.close();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		int serializedUckeleoidCount = w2.getCount(WorldObject.UCKELEOID);
		System.out.println("Serialized Uckeleoid count: "+serializedUckeleoidCount);
		System.exit(0);
	}
}
