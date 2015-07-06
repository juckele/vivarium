package com.johnuckele.vivarium.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class World implements Serializable
{
	/**
	 * serialVersion
	 */
	private static final long		serialVersionUID					= 4L;

	private WorldVariables			_worldVariables;

	private int						_maximumCreatureID;
	private int						_tickCounter;
	protected int					_worldDimensions;

	protected WorldObject[][]		_worldObjectGrid;
	protected Creature[][]			_creatureGrid;
	protected LinkedList<Creature>	_liveCreatureList;
	private LinkedList<Creature>	_deadCreatureList;
	
	// Auxiliary data strucures
	private CensusRecord			_census;
	private LinkedList<ActionProfile> _generationalMaleActionProfiles;
	private LinkedList<ActionProfile> _generationalFemaleActionProfiles;

	public World(int worldDimensions, WorldVariables worldVariables)
	{
		// Set up base variables
		this._worldVariables = worldVariables;
		this._maximumCreatureID = 0;
		this._tickCounter = 0;

		// Size the world
		this.setWorldDimensions(worldDimensions);

		// Build the action profile data structures
		if(this._worldVariables.getKeepGenerationActionProfile())
		{
			_generationalMaleActionProfiles = new LinkedList<ActionProfile>();
			_generationalFemaleActionProfiles = new LinkedList<ActionProfile>();
		}

		// Final step
		this.populatateWorld();

		// Take an initial census
		if(this._worldVariables.getKeepCensusData())
		{
			this._census = new CensusRecord(this.getCount(WorldObject.CREATURE));
		}
	}

	public int getWorldDimensions()
	{
		return this._worldDimensions;
	}

	private void populatateWorld()
	{
		ArrayList<Species> species = _worldVariables.getSpecies();
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				_worldObjectGrid[r][c] = WorldObject.EMPTY;
				_creatureGrid[r][c] = null;
				if(r < 1 || c < 1 || r > _worldDimensions - 2 || c > _worldDimensions - 2)
				{
					_worldObjectGrid[r][c] = WorldObject.WALL;
				}
				else
				{
					double randomNumber = Rand.getRandomPositiveDouble();
					double wallProbability = _worldVariables.getInitialWallGenerationProbability();
					double foodProbability = _worldVariables.getInitialFoodGenerationProbability();
					double creatureProbability = _worldVariables.getInitialCreatureGenerationProbability();
					if(randomNumber < wallProbability)
					{
						_worldObjectGrid[r][c] = WorldObject.WALL;
					}
					else if(randomNumber < wallProbability + foodProbability)
					{
						_worldObjectGrid[r][c] = WorldObject.FOOD;
					}
					else if(randomNumber < wallProbability + foodProbability + creatureProbability)
					{
						_worldObjectGrid[r][c] = WorldObject.CREATURE;
						Creature creature = new Creature(this, defaultSpecies, r, c);
						_creatureGrid[r][c] = creature;
						_liveCreatureList.add(creature);
					}
				}
			}
		}
	}

	public int requestNewCreatureID()
	{
		return(++_maximumCreatureID);
	}

	/**
	 * Top level simulation step of the entire world and all denizens within it.
	 * Simulations are divided into four phases: 1, each creature will age and
	 * compute other time based values. 2, each creatur will decide on an
	 * action to attempt. 3, each creature will attempt to execute the planned
	 * action (the order of execution on the actions is currently left to right,
	 * top to bottom, so some creature will get priority if actions conflict).
	 * 4, finally, food is spawned at a constant chance in empty spaces in the
	 * world.
	 */
	public void tick()
	{
		// Each creature calculates time based
		// changes in condition such as age,
		// gestation, and energy levels.
		tickCreatures();

		// Creatures transmit sound
		transmitSounds();
		
		// Each creature plans which actions to
		// attempt to do during the next phase
		letCreaturesPlan();

		// Each creature will physically try to carry
		// out the planned action
		executeCreaturePlans();

		// New food resources will be spawned in the world
		spawnFood();

		// Keep the census up to date
		this._tickCounter++;
		if(this._worldVariables.getKeepCensusData())
		{
			this._census.updateRecords(this._tickCounter, this._liveCreatureList.size());
		}
	}

	private void tickCreatures()
	{
		for(int r = 0; r < _worldDimensions; r++)
		{
			for(int c = 0; c < _worldDimensions; c++)
			{
				if(_worldObjectGrid[r][c] == WorldObject.CREATURE)
				{
					_creatureGrid[r][c].tick();
				}
			}
		}
	}

	private void transmitSounds()
	{
		for(Creature speaker : this._liveCreatureList)
			{
			for(Creature listener : this._liveCreatureList)
			{
					if( listener != speaker )
					{
						listener.listenToCreature(speaker);
					}
				}
			}
	}

	private void letCreaturesPlan()
	{
		for(Creature creature : this._liveCreatureList)
		{
			creature.planAction();
		}
	}

	private void executeCreaturePlans()
	{
		// Creatures act
		LinkedList<Creature> dyingCreatures = new LinkedList<Creature>();
		LinkedList<Creature> spawningCreatures = new LinkedList<Creature>();
		for(Creature creature : this._liveCreatureList)
		{
			Action action = creature.getAction();
			Direction facing = creature.getFacing();
			int r = creature.getR();
			int c = creature.getC();
			int facingR = r + Direction.getVerticalComponent(facing);
			int facingC = c + Direction.getHorizontalComponent(facing);
			// Death
			if(action == Action.DIE)
			{
				creature.executeAction(action);
				removeObject(r, c);
				dyingCreatures.add(creature);
			}
			// Various actions that always succeed and are simple
			else if(action == Action.TURN_LEFT || action == Action.TURN_RIGHT || action == Action.REST)
			{
				creature.executeAction(action);
			}
			// Movement
			else if(action == Action.MOVE && _worldObjectGrid[facingR][facingC] == WorldObject.EMPTY)
			{
				creature.executeAction(action);
				moveObject(r, c, facing);
			}
			// Eating
			else if(action == Action.EAT && _worldObjectGrid[facingR][facingC] == WorldObject.FOOD)
			{
				creature.executeAction(action);
				removeObject(r, c, facing);
			}
			// Attempt to breed
			else if(action == Action.BREED
			// Make sure we're facing another creature
					&& _worldObjectGrid[facingR][facingC] == WorldObject.CREATURE
					// And that creature also is trying to breed
					&& _creatureGrid[facingR][facingC].getAction() == Action.BREED
					// Make sure the creatures are facing each other
					&& creature.getFacing() == Direction.flipDirection(_creatureGrid[facingR][facingC].getFacing()))
			{
				creature.executeAction(action, _creatureGrid[facingR][facingC]);
			}
			// Giving Birth
			else if(action == Action.BIRTH && _worldObjectGrid[facingR][facingC] == WorldObject.EMPTY)
			{
				Creature spawningCreature = creature.getFetus();
				creature.executeAction(action);
				setCreature(spawningCreature, facingR, facingC);
				spawningCreatures.add(spawningCreature);
			}
			// Action failed
			else
			{
				creature.failAction(action);
			}
		}
		// Remove dead creatures
		for(Creature deadCreatures : dyingCreatures)
		{
			this._liveCreatureList.remove(deadCreatures);
			if(this._worldVariables.getRememberTheDead())
			{
				this._deadCreatureList.add(deadCreatures);
			}
		}
		// Add newborn rats
		for(Creature spawningCreature : spawningCreatures)
		{
			this._liveCreatureList.add(spawningCreature);
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
					double randomNumber = Rand.getRandomPositiveDouble();
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
		// Special creatures move extras
		if(_worldObjectGrid[r2][c2] == WorldObject.CREATURE)
		{
			_creatureGrid[r2][c2] = _creatureGrid[r1][c1];
			_creatureGrid[r1][c1] = null;
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
		_creatureGrid[r][c] = null;
	}

	public LinkedList<Creature> getAllCreatures()
	{
		LinkedList<Creature> allCreatures = new LinkedList<Creature>();
		allCreatures.addAll(this._liveCreatureList);
		allCreatures.addAll(this._deadCreatureList);
		Collections.sort(allCreatures, new Comparator<Creature>()
		{
			@Override public int compare(Creature o1, Creature o2)
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
		return allCreatures;
	}

	public CensusRecord getCensus()
	{
		if(this._worldVariables.getKeepCensusData())
		{
			return this._census;
		}
		else 
		{
			throw new Error("Census data not available due to world configuration settings");
		}
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

	public int getMaximimCreatureID()
	{
		return this._maximumCreatureID;
	}

	public void setMaximumCreatureID(int maximumCreatureID)
	{
		this._maximumCreatureID = maximumCreatureID;
	}

	public int getTickCounter()
	{
		return this._tickCounter;
	}

	public void setTickCounter(int tickCounter)
	{
		this._tickCounter = tickCounter;
	}

	public Creature getCreature(int r, int c)
	{
		return this._creatureGrid[r][c];
	}

	public void addCreatureAtPosition(Creature creature, int r, int c)
	{
		setObject(WorldObject.CREATURE, creature, r, c);
		this._liveCreatureList.add(creature);
	}

	private void setCreature(Creature creature, int r, int c)
	{
		setObject(WorldObject.CREATURE, creature, r, c);
	}

	public void setWorldDimensions(int worldDimensions)
	{
		this._worldDimensions = worldDimensions;

		this._worldObjectGrid = new WorldObject[_worldDimensions][_worldDimensions];
		this._creatureGrid = new Creature[_worldDimensions][_worldDimensions];
		this._liveCreatureList = new LinkedList<Creature>();
		this._deadCreatureList = new LinkedList<Creature>();
	}

	public WorldObject getWorldObject(int r, int c)
	{
		return(this._worldObjectGrid[r][c]);
	}

	public void setObject(WorldObject obj, int r, int c)
	{
		if(obj == WorldObject.CREATURE)
		{
			throw new Error("Creature WorldObjects should not be assinged directly, use setCreature");
		}
		setObject(obj, null, r, c);
	}

	public WorldVariables getWorldVariables()
	{
		return _worldVariables;
	}

	private void setObject(WorldObject obj, Creature creature, int r, int c)
	{
		_worldObjectGrid[r][c] = obj;
		if(creature != null)
		{
			creature.setPosition(r, c);
		}
		_creatureGrid[r][c] = creature;
	}

	public ActionProfile getActionProfileForGeneration(int generation, Gender gender)
	{
		// Populate new generation trackers as required
		while(_generationalFemaleActionProfiles.size() < generation)
		{
			_generationalFemaleActionProfiles.add(new ActionProfile());
			_generationalMaleActionProfiles.add(new ActionProfile());
		}
		// Get the requested generationalActionProfile
		if(gender == Gender.FEMALE)
		{
			return _generationalFemaleActionProfiles.get(generation-1);
		}
		else
		{
			return _generationalMaleActionProfiles.get(generation-1);
		}
	}

	public LinkedList<ActionProfile> getAllActionProfilesForGender(Gender gender)
	{
		if(this._worldVariables.getKeepGenerationActionProfile())
		{
			LinkedList<ActionProfile> actionProfiles = new LinkedList<ActionProfile>();
			if(gender == Gender.FEMALE)
			{
				actionProfiles.addAll(this._generationalFemaleActionProfiles);
			}
			if(gender == Gender.MALE)
			{
				actionProfiles.addAll(this._generationalMaleActionProfiles);
			}
			return actionProfiles;
		}
		else 
		{
			throw new Error("Generational Action Profile data not available due to world configuration settings");
		}
	}

	
	public String toString(RenderCode code)
	{
		if(code == RenderCode.WORLD_MAP)
		{
			return(renderMap());
		}
		else if(code == RenderCode.BRAIN_WEIGHTS)
		{
			return(renderBrainWeights());
		}
		else if(code == RenderCode.FULL_CREATURE_LIST)
		{
			StringBuilder creatureOutput = new StringBuilder();
			for(Creature creature : this.getAllCreatures())
			{
				creatureOutput.append(creature.toString(RenderCode.SIMPLE_CREATURE));
				creatureOutput.append('\n');
			}
			return(creatureOutput.toString());
		}
		else if(code == RenderCode.LIVE_CREATURE_LIST)
		{
			StringBuilder creatureOutput = new StringBuilder();
			for(Creature creature : this._liveCreatureList)
			{
				creatureOutput.append(creature.toString(RenderCode.SIMPLE_CREATURE));
				creatureOutput.append('\n');
			}
			return(creatureOutput.toString());
		}
		else if(code == RenderCode.DEAD_CREATURE_LIST)
		{
			StringBuilder creatureOutput = new StringBuilder();
			for(Creature creature : this._deadCreatureList)
			{
				creatureOutput.append(creature.toString(RenderCode.SIMPLE_CREATURE));
				creatureOutput.append('\n');
			}
			return(creatureOutput.toString());
		}
		else
		{
			throw new Error("Invalid Code "+code);
		}
	}

	private String renderMap()
	{
		// Draw world map
		StringBuilder worldOutput = new StringBuilder();
		worldOutput.append("Walls: ");
		worldOutput.append(this.getCount(WorldObject.WALL));
		worldOutput.append(", Creatures: ");
		worldOutput.append(this.getCount(WorldObject.CREATURE));
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
				else if(_worldObjectGrid[r][c] == WorldObject.CREATURE)
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
		// Draw creature readouts
		StringBuilder brainOutput = new StringBuilder();
		LinkedList<NeuralNetworkBrain> brains = new LinkedList<NeuralNetworkBrain>();
		for(Creature rat : this._liveCreatureList)
		{
			brains.add(rat.getBrain());
		}
		if(brains.size() > 0)
		{
//			Brain minBrain = Brain.minBrain(brains);
//			Brain maxBrain = Brain.maxBrain(brains);
			NeuralNetworkBrain medianBrain = NeuralNetworkBrain.medianBrain(brains);
			NeuralNetworkBrain standardDeviationBrain = NeuralNetworkBrain.standardDeviationBrain(brains, medianBrain);
			brainOutput.append("Average creature NN:\n");
			brainOutput.append(medianBrain.toString());
			brainOutput.append("Std. Deviation on creature NNs:\n");
			brainOutput.append(standardDeviationBrain.toString());
/*			brainOutput.append("Min creature NN:\n");
			brainOutput.append(minBrain.toString());
			brainOutput.append("Max creature NN:\n");
			brainOutput.append(maxBrain.toString());*/
			brainOutput.append("Oldest creature NN:\n");
			brainOutput.append(brains.get(0).toString());
			brainOutput.append("Random creature NN:\n");
			brainOutput.append(brains.get(Rand.getRandomInt(brains.size())).toString());
		}
		return(brainOutput.toString());
	}
}
