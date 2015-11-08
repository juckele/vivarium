/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import io.vivarium.audit.AuditFunction;
import io.vivarium.audit.AuditRecord;
import io.vivarium.core.brain.Brain;
import io.vivarium.core.brain.BrainType;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.util.Rand;
import io.vivarium.visualization.RenderCode;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class World extends VivariumObject
{
    @SerializedParameter
    private int _maximumCreatureID;
    @SerializedParameter
    private int _tick;
    @SerializedParameter
    private int _width;
    @SerializedParameter
    private int _height;

    @SerializedParameter
    protected EntityType[][] _entityGrid;
    @SerializedParameter
    protected Creature[][] _creatureGrid;
    @SerializedParameter
    protected AuditRecord[] _auditRecords;

    @SerializedParameter
    private Blueprint _blueprint;

    protected World()
    {
    }

    public World(Blueprint blueprint)
    {
        // Store the blueprint
        this._blueprint = blueprint;

        // Set up base variables
        this._maximumCreatureID = 0;

        // Size the world
        this.setWorldDimensions(blueprint.getWidth(), blueprint.getHeight());

        // Fill the world with creatures and food
        this.populatateWorld();

        // Build audit records
        this.constructAuditRecords();
        this.performAudits();
    }

    private void constructAuditRecords()
    {
        int auditRecordCount = _blueprint.getSpecies().size() * _blueprint.getAuditFunctions().size();
        _auditRecords = new AuditRecord[auditRecordCount];
        int i = 0;
        for (Species species : _blueprint.getSpecies())
        {
            for (AuditFunction auditFunction : _blueprint.getAuditFunctions())
            {
                _auditRecords[i] = auditFunction.getAuditType().makeRecordWithSpecies(auditFunction, species);
                i++;
            }
        }
    }

    private void populatateWorld()
    {
        WorldPopulator populator = new WorldPopulator();
        populator.setSpecies(_blueprint.getSpecies());
        populator.setWallProbability(_blueprint.getInitialWallGenerationProbability());
        populator.setFoodProbability(_blueprint.getInitialFoodGenerationProbability());
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                setObject(EntityType.EMPTY, r, c);
                _creatureGrid[r][c] = null;
                if (r < 1 || c < 1 || r > _height - 2 || c > _width - 2)
                {
                    setObject(EntityType.WALL, r, c);
                }
                else
                {
                    EntityType object = populator.getNextEntityType();
                    if (object == EntityType.CREATURE)
                    {
                        Species species = populator.getNextCreatureSpecies();
                        Creature creature = new Creature(species);
                        addCreature(creature, r, c);
                    }
                    else
                    {
                        setObject(object, r, c);
                    }
                }
            }
        }
    }

    public int getNewCreatureID()
    {
        return (++_maximumCreatureID);
    }

    /**
     * Top level simulation step of the entire world and all denizens within it. Simulations are divided into four
     * phases: 1, each creature will age and compute other time based values. 2, each creatur will decide on an action
     * to attempt. 3, each creature will attempt to execute the planned action (the order of execution on the actions is
     * currently left to right, top to bottom, so some creature will get priority if actions conflict). 4, finally, food
     * is spawned at a constant chance in empty spaces in the world.
     */
    public void tick()
    {
        // Increment tick counter
        _tick++;

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

        // Record with audit records
        performAudits();
    }

    private void tickCreatures()
    {
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (_entityGrid[r][c] == EntityType.CREATURE)
                {
                    _creatureGrid[r][c].tick();
                }
            }
        }
    }

    private void transmitSounds()
    {
        if (this._blueprint.getSoundEnabled())
        {
            for (int r = 0; r < this._height; r++)
            {
                for (int c = 0; c < this._width; c++)
                {
                    if (_entityGrid[r][c] == EntityType.CREATURE)
                    {
                        transmitSoundsFrom(r, c);
                    }
                }
            }
        }
    }

    private void transmitSoundsFrom(int r1, int c1)
    {
        for (int c2 = c1 + 1; c2 < this._width; c2++)
        {
            int r2 = r1;
            if (_entityGrid[r2][c2] == EntityType.CREATURE)
            {
                transmitSoundsFromTo(r1, c1, r2, c2);
            }
        }
        for (int r2 = r1 + 1; r2 < this._height; r2++)
        {
            for (int c2 = c1; c2 < this._width; c2++)
            {
                if (_entityGrid[r2][c2] == EntityType.CREATURE)
                {
                    transmitSoundsFromTo(r1, c1, r2, c2);
                }
            }
        }
    }

    private void transmitSoundsFromTo(int r1, int c1, int r2, int c2)
    {
        int distanceSquared = (r1 - r2) * (r1 - r2) + (c1 - c2) * (c1 - c2);
        _creatureGrid[r1][c1].listenToCreature(_creatureGrid[r2][c2], distanceSquared);
        _creatureGrid[r2][c2].listenToCreature(_creatureGrid[r1][c1], distanceSquared);
    }

    private void letCreaturesPlan()
    {
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (_entityGrid[r][c] == EntityType.CREATURE)
                {
                    _creatureGrid[r][c].planAction(this, r, c);
                }
            }
        }
    }

    private void executeCreaturePlans()
    {
        // Creatures act
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (_entityGrid[r][c] == EntityType.CREATURE)
                {
                    if (!_creatureGrid[r][c].hasActed())
                    {
                        executeCreaturePlan(r, c);
                    }
                }
            }
        }
    }

    private void executeCreaturePlan(int r, int c)
    {
        Creature creature = _creatureGrid[r][c];
        Action action = creature.getAction();
        Direction facing = creature.getFacing();
        int facingR = r + Direction.getVerticalComponent(facing);
        int facingC = c + Direction.getHorizontalComponent(facing);
        // Death
        if (action == Action.DIE)
        {
            creature.executeAction(action);
            killCreature(r, c);
        }
        // Various actions that always succeed and are simple
        else if (action == Action.TURN_LEFT || action == Action.TURN_RIGHT || action == Action.REST)
        {
            creature.executeAction(action);
        }
        // Movement
        else if (action == Action.MOVE && _entityGrid[facingR][facingC] == EntityType.EMPTY)
        {
            creature.executeAction(action);
            moveObject(r, c, facing);
        }
        // Eating
        else if (action == Action.EAT && _entityGrid[facingR][facingC] == EntityType.FOOD)
        {
            creature.executeAction(action);
            removeObject(r, c, facing);
        }
        // Attempt to breed
        else if (action == Action.BREED
                // Make sure we're facing another creature
                && _entityGrid[facingR][facingC] == EntityType.CREATURE
                // And that creature is the same species as us
                && _creatureGrid[facingR][facingC].getSpecies() == creature.getSpecies()
                // And that creature also is trying to breed
                && _creatureGrid[facingR][facingC].getAction() == Action.BREED
                // And that creature is the opposite gender
                && _creatureGrid[facingR][facingC].getIsFemale() != creature.getIsFemale()
                // Make sure the creatures are facing each other
                && creature.getFacing() == Direction.flipDirection(_creatureGrid[facingR][facingC].getFacing()))
        {
            creature.executeAction(action, _creatureGrid[facingR][facingC]);
        }
        // Giving Birth
        else if (action == Action.BIRTH && _entityGrid[facingR][facingC] == EntityType.EMPTY)
        {
            Creature spawningCreature = creature.getFetus();
            creature.executeAction(action);
            addCreature(spawningCreature, facingR, facingC);
        }
        // Action failed
        else
        {
            creature.failAction(action);
        }
    }

    private void spawnFood()
    {
        // Generate food at a given rate
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (_entityGrid[r][c] == EntityType.EMPTY)
                {
                    double randomNumber = Rand.getInstance().getRandomPositiveDouble();
                    if (randomNumber < this._blueprint.getFoodGenerationProbability())
                    {
                        setObject(EntityType.FOOD, r, c);
                        _entityGrid[r][c] = EntityType.FOOD;
                    }
                }
            }
        }
    }

    private void performAudits()
    {
        for (int i = 0; i < _auditRecords.length; i++)
        {
            _auditRecords[i].record(this, _tick);
        }
    }

    public int getTickCounter()
    {
        return _tick;
    }

    private void moveObject(int r, int c, Direction direction)
    {
        int r1 = r;
        int c1 = c;
        int r2 = r;
        int c2 = c;
        switch (direction)
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
        _entityGrid[r2][c2] = _entityGrid[r1][c1];
        _entityGrid[r1][c1] = EntityType.EMPTY;
        // Special creatures move extras
        if (_entityGrid[r2][c2] == EntityType.CREATURE)
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
        switch (direction)
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

        _entityGrid[r][c] = EntityType.EMPTY;
        _creatureGrid[r][c] = null;
    }

    public LinkedList<AuditRecord> getAuditRecords()
    {
        LinkedList<AuditRecord> auditRecords = new LinkedList<AuditRecord>();
        for (int i = 0; i < this._auditRecords.length; i++)
        {
            auditRecords.add(_auditRecords[i]);
        }
        return auditRecords;
    }

    public LinkedList<Creature> getCreatures()
    {
        LinkedList<Creature> allCreatures = new LinkedList<Creature>();
        for (int r = 0; r < this._height; r++)
        {
            for (int c = 0; c < this._width; c++)
            {
                if (_entityGrid[r][c] == EntityType.CREATURE)
                {
                    allCreatures.add(_creatureGrid[r][c]);
                }
            }
        }
        Collections.sort(allCreatures, new Comparator<Creature>()
        {
            @Override
            public int compare(Creature c1, Creature c2)
            {
                int generationComparison = Double.compare(c1.getGeneration(), c2.getGeneration());
                if (generationComparison != 0)
                {
                    return generationComparison;
                }
                else
                {
                    return Integer.compare(c1.getID(), c2.getID());
                }
            }
        });
        return allCreatures;
    }

    public int getCount(EntityType obj)
    {
        int count = 0;
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._entityGrid[r][c] == obj)
                {
                    count++;
                }
            }
        }
        return (count);
    }

    public int getCount(Species s)
    {
        int count = 0;
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._creatureGrid[r][c] != null && this._creatureGrid[r][c].getSpecies().equals(s))
                {
                    count++;
                }
            }
        }
        return (count);
    }

    public int getMaximimCreatureID()
    {
        return this._maximumCreatureID;
    }

    public void setMaximumCreatureID(int maximumCreatureID)
    {
        this._maximumCreatureID = maximumCreatureID;
    }

    public Creature getCreature(int r, int c)
    {
        return this._creatureGrid[r][c];
    }

    private void addCreature(Creature creature, int r, int c)
    {
        creature.setID(this.getNewCreatureID());
        setObject(EntityType.CREATURE, creature, r, c);
    }

    private void killCreature(int r, int c)
    {
        removeObject(r, c);
    }

    public void addImmigrant(Creature creature)
    {
        boolean immigrantPlaced = false;
        while (!immigrantPlaced)
        {
            int r = Rand.getInstance().getRandomInt(this._height);
            int c = Rand.getInstance().getRandomInt(this._width);
            if (_entityGrid[r][c] == EntityType.EMPTY)
            {
                addCreature(creature, r, c);
                immigrantPlaced = true;
            }
        }
    }

    public int getWorldWidth()
    {
        return this._width;
    }

    public int getWorldHeight()
    {
        return this._height;
    }

    public void setWorldDimensions(int width, int height)
    {
        this._width = width;
        this._height = height;

        this._entityGrid = new EntityType[height][width];
        this._creatureGrid = new Creature[height][width];
    }

    public EntityType getEntityType(int r, int c)
    {
        return (this._entityGrid[r][c]);
    }

    public void setObject(EntityType obj, int r, int c)
    {
        if (obj == EntityType.CREATURE)
        {
            throw new Error("Creature EntityTypes should not be assinged directly, use setCreature");
        }
        setObject(obj, null, r, c);
    }

    private void setObject(EntityType obj, Creature creature, int r, int c)
    {
        _entityGrid[r][c] = obj;
        _creatureGrid[r][c] = creature;
    }

    public String render(RenderCode code)
    {
        if (code == RenderCode.WORLD_MAP)
        {
            return (renderMap());
        }
        else if (code == RenderCode.BRAIN_WEIGHTS)
        {
            return (renderBrainWeights());
        }
        else if (code == RenderCode.LIVE_CREATURE_LIST)
        {
            StringBuilder creatureOutput = new StringBuilder();
            for (int r = 0; r < this._height; r++)
            {
                for (int c = 0; c < this._width; c++)
                {
                    if (_entityGrid[r][c] == EntityType.CREATURE)
                    {
                        creatureOutput.append(_creatureGrid[r][c].render(RenderCode.SIMPLE_CREATURE, r, c));
                        creatureOutput.append('\n');
                    }
                }
            }
            return (creatureOutput.toString());
        }
        else
        {
            throw new IllegalArgumentException("RenderCode " + code + " not supported for type " + this.getClass());
        }
    }

    private String renderMap()
    {
        String[] glyphs = { "中", "马", "心" };
        // Draw world map
        StringBuilder worldOutput = new StringBuilder();
        worldOutput.append("Walls: ");
        worldOutput.append(this.getCount(EntityType.WALL));
        HashMap<Species, String> speciesToGlyphMap = new HashMap<Species, String>();
        for (Species s : this._blueprint.getSpecies())
        {
            speciesToGlyphMap.put(s, glyphs[speciesToGlyphMap.size()]);
            worldOutput.append(", ").append(speciesToGlyphMap.get(s)).append("-creatures: ");
            worldOutput.append(this.getCount(s));
        }
        worldOutput.append(", Food: ");
        worldOutput.append(this.getCount(EntityType.FOOD));
        worldOutput.append('\n');
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (_entityGrid[r][c] == EntityType.EMPTY)
                {
                    worldOutput.append('\u3000');
                }
                else if (_entityGrid[r][c] == EntityType.WALL)
                {
                    worldOutput.append('口');
                }
                else if (_entityGrid[r][c] == EntityType.FOOD)
                {
                    worldOutput.append('一');
                }
                else if (_entityGrid[r][c] == EntityType.CREATURE)
                {
                    worldOutput.append(speciesToGlyphMap.get(_creatureGrid[r][c].getSpecies()));
                }
            }
            worldOutput.append('\n');
        }
        return (worldOutput.toString());
    }

    private String renderBrainWeights()
    {
        StringBuilder multiSpeciesOutput = new StringBuilder();
        for (Species species : this._blueprint.getSpecies())
        {
            multiSpeciesOutput.append(this.renderBrainWeights(species));
        }
        return multiSpeciesOutput.toString();
    }

    private String renderBrainWeights(Species s)
    {
        // Draw average brain
        // Draw creature readouts
        LinkedList<Brain> brains = new LinkedList<Brain>();
        for (int r = 0; r < this._height; r++)
        {
            for (int c = 0; c < this._width; c++)
            {
                if (_entityGrid[r][c] == EntityType.CREATURE && _creatureGrid[r][c].getSpecies().equals(s))
                {
                    brains.add(_creatureGrid[r][c].getBrain());
                }
            }
        }
        if (brains.size() > 0)
        {
            return BrainType.render(brains.getFirst().getBrainType(), brains);
        }
        return "";
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
