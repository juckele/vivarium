package com.johnuckele.vivarium.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.audit.AuditFunction;
import com.johnuckele.vivarium.audit.AuditRecord;
import com.johnuckele.vivarium.core.brain.Brain;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class World implements MapSerializer
{
    private int   _maximumCreatureID;
    private int   _tick;
    protected int _worldDimensions;

    protected EntityType[][] _entityGrid;
    protected Creature[][]   _creatureGrid;
    protected AuditRecord[]  _auditRecords;

    private Blueprint _blueprint;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("maximumCreatureID", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("tick", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("worldDimensions", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("entityGrid", EntityType[][].class));
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("auditRecords", AuditRecord[].class, SerializationCategory.AUDIT_RECORD));
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("creatureGrid", Creature[][].class, SerializationCategory.CREATURE));
        SERIALIZED_PARAMETERS
                .add(new SerializedParameter("blueprint", Blueprint.class, SerializationCategory.BLUEPRINT));
    }

    private World()
    {
    }

    public World(Blueprint blueprint)
    {
        // Store the blueprint
        this._blueprint = blueprint;

        // Set up base variables
        this._maximumCreatureID = 0;

        // Size the world
        this.setWorldDimensions(blueprint.getSize());

        // Fill the world with creatures and food
        this.populatateWorld();

        // Build audit records
        this.constructAuditRecords();
        this.performAudits();
    }

    public int getWorldDimensions()
    {
        return this._worldDimensions;
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
                _auditRecords[i] = auditFunction.getAuditType().makeWithSpecies(species);
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
            {
                setObject(EntityType.EMPTY, r, c);
                _creatureGrid[r][c] = null;
                if (r < 1 || c < 1 || r > _worldDimensions - 2 || c > _worldDimensions - 2)
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
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
            for (int r = 0; r < this._worldDimensions; r++)
            {
                for (int c = 0; c < this._worldDimensions; c++)
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
        for (int c2 = c1 + 1; c2 < this._worldDimensions; c2++)
        {
            int r2 = r1;
            if (_entityGrid[r2][c2] == EntityType.CREATURE)
            {
                transmitSoundsFromTo(r1, c1, r2, c2);
            }
        }
        for (int r2 = r1 + 1; r2 < this._worldDimensions; r2++)
        {
            for (int c2 = c1; c2 < this._worldDimensions; c2++)
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
            {
                if (_entityGrid[r][c] == EntityType.EMPTY)
                {
                    double randomNumber = Rand.getRandomPositiveDouble();
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
        for (int r = 0; r < this._worldDimensions; r++)
        {
            for (int c = 0; c < this._worldDimensions; c++)
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
                if (c1.getGeneration() > c2.getGeneration())
                {
                    return 1;
                }
                else if (c1.getGeneration() < c2.getGeneration())
                {
                    return -1;
                }
                else
                {
                    if (c1.getID() > c2.getID())
                    {
                        return 1;
                    }
                    else if (c1.getID() < c2.getID())
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        });
        return allCreatures;
    }

    public int getCount(EntityType obj)
    {
        int count = 0;
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
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
            int r = Rand.getRandomInt(this._worldDimensions);
            int c = Rand.getRandomInt(this._worldDimensions);
            if (_entityGrid[r][c] == EntityType.EMPTY)
            {
                addCreature(creature, r, c);
                immigrantPlaced = true;
            }
        }
    }

    public void setWorldDimensions(int worldDimensions)
    {
        this._worldDimensions = worldDimensions;

        this._entityGrid = new EntityType[_worldDimensions][_worldDimensions];
        this._creatureGrid = new Creature[_worldDimensions][_worldDimensions];
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
            for (int r = 0; r < this._worldDimensions; r++)
            {
                for (int c = 0; c < this._worldDimensions; c++)
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
        for (int r = 0; r < _worldDimensions; r++)
        {
            for (int c = 0; c < _worldDimensions; c++)
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
        for (int r = 0; r < this._worldDimensions; r++)
        {
            for (int c = 0; c < this._worldDimensions; c++)
            {
                if (_entityGrid[r][c] == EntityType.CREATURE && _creatureGrid[r][c].getSpecies().equals(s))
                {
                    brains.add(_creatureGrid[r][c].getBrain());
                }
            }
        }
        if (brains.size() > 0)
        {
            return brains.getFirst().getBrainType().render(brains);
        }
        return "";
    }

    @Override
    public List<MapSerializer> getReferences()
    {
        LinkedList<MapSerializer> list = new LinkedList<MapSerializer>();
        list.add(_blueprint);
        list.addAll(getCreatures());
        return list;
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return World.SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "maximumCreatureID":
                return _maximumCreatureID;
            case "tick":
                return _tick;
            case "worldDimensions":
                return _worldDimensions;
            case "entityGrid":
                return _entityGrid;
            case "auditRecords":
                return _auditRecords;
            case "creatureGrid":
                return _creatureGrid;
            case "blueprint":
                return _blueprint;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "maximumCreatureID":
                _maximumCreatureID = (Integer) value;
                break;
            case "tick":
                _tick = (Integer) value;
                break;
            case "worldDimensions":
                _worldDimensions = (Integer) value;
                break;
            case "auditRecords":
                _auditRecords = (AuditRecord[]) value;
                break;
            case "entityGrid":
                _entityGrid = (EntityType[][]) value;
                break;
            case "creatureGrid":
                _creatureGrid = (Creature[][]) value;
                break;
            case "blueprint":
                _blueprint = (Blueprint) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.WORLD;
    }

    public static World makeUninitialized()
    {
        return new World();
    }

    public static World makeCopy(World original)
    {
        return (World) new SerializationEngine().makeCopy(original);
    }
}
