package io.vivarium.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.audit.AuditRecord;
import io.vivarium.core.WorldPopulator.EntityType;
import io.vivarium.core.processor.Processor;
import io.vivarium.core.processor.ProcessorType;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
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
    protected ItemType[][] _itemGrid;
    @SerializedParameter
    protected Creature[][] _creatureGrid;
    @SerializedParameter
    protected TerrainType[][] _terrainGrid;
    @SerializedParameter
    protected AuditRecord[] _auditRecords;

    @SerializedParameter
    private WorldBlueprint _worldBlueprint;

    protected World()
    {
    }

    public World(WorldBlueprint worldBlueprint)
    {
        // Store the world blueprint
        this._worldBlueprint = worldBlueprint;

        // Set up base variables
        this._maximumCreatureID = 0;

        // Size the world
        this.setWorldDimensions(worldBlueprint.getWidth(), worldBlueprint.getHeight());

        // Fill the world with creatures and food
        this.populatateWorld();

        // Build audit records
        this.constructAuditRecords();
        this.performAudits();
    }

    private void constructAuditRecords()
    {
        int auditRecordCount = _worldBlueprint.getCreatureBlueprints().size()
                * _worldBlueprint.getAuditBlueprints().size();
        _auditRecords = new AuditRecord[auditRecordCount];
        int i = 0;
        for (CreatureBlueprint creatureBlueprint : _worldBlueprint.getCreatureBlueprints())
        {
            for (AuditBlueprint auditBlueprint : _worldBlueprint.getAuditBlueprints())
            {
                _auditRecords[i] = auditBlueprint.makeRecordWithCreatureBlueprint(creatureBlueprint);
                i++;
            }
        }
    }

    private void populatateWorld()
    {
        WorldPopulator populator = new WorldPopulator();
        populator.setCreatureBlueprints(_worldBlueprint.getCreatureBlueprints());
        populator.setWallProbability(_worldBlueprint.getInitialWallGenerationProbability());
        populator.setFoodProbability(_worldBlueprint.getInitialFoodGenerationProbability());
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                _creatureGrid[r][c] = null;
                if (r < 1 || c < 1 || r > _height - 2 || c > _width - 2)
                {
                    setTerrain(TerrainType.WALL, r, c);
                }
                else
                {
                    EntityType type = populator.getNextEntityType();
                    if (type == EntityType.CREATURE)
                    {
                        CreatureBlueprint creatureBlueprint = populator.getNextCreatureBlueprint();
                        Creature creature = new Creature(creatureBlueprint);
                        addCreature(creature, r, c);
                    }
                    else if (type == EntityType.ITEM)
                    {
                        // TODO: Add non-food items
                        setItem(ItemType.FOOD, r, c);
                    }
                    else if (type == EntityType.TERRAIN)
                    {
                        // TODO: Add non-wall terrain
                        setTerrain(TerrainType.WALL, r, c);
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

        // Creatures transmit sound & signs
        transmitSounds();
        transmitSigns();

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
        for (int r = 1; r < _height - 1; r++)
        {
            for (int c = 1; c < _width - 1; c++)
            {
                if (_creatureGrid[r][c] != null)
                {
                    _creatureGrid[r][c].tick();
                }
            }
        }
    }

    private void transmitSounds()
    {
        if (this._worldBlueprint.getSoundEnabled())
        {
            for (int r = 1; r < this._height - 1; r++)
            {
                for (int c = 1; c < this._width - 1; c++)
                {
                    if (_creatureGrid[r][c] != null)
                    {
                        transmitSoundsFrom(r, c);
                    }
                }
            }
        }
    }

    private void transmitSoundsFrom(int r1, int c1)
    {
        // We transmit sounds both directions at the same time, so we only want to get each pair of
        // creatures once. Anything 'below and to the right' is should be a pair that haven't shared
        // sounds yet.
        for (int c2 = c1 + 1; c2 < this._width; c2++)
        {
            int r2 = r1;
            if (_creatureGrid[r1][c1] != null)
            {
                transmitSoundsFromTo(r1, c1, r2, c2);
            }
        }
        for (int r2 = r1 + 1; r2 < this._height; r2++)
        {
            for (int c2 = c1; c2 < this._width; c2++)
            {
                if (_creatureGrid[r2][c2] != null)
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

    private void transmitSigns()
    {
        if (this._worldBlueprint.getSignEnabled())
        {
            for (int r = 1; r < this._height - 1; r++)
            {
                for (int c = 1; c < this._width - 1; c++)
                {
                    if (_creatureGrid[r][c] != null)
                    {
                        transmitSignsFrom(r, c);
                    }
                }
            }
        }
    }

    private void transmitSignsFrom(int r1, int c1)
    {
        Direction facing = _creatureGrid[r1][c1].getFacing();
        int r2 = r1 + Direction.getVerticalComponent(facing);
        int c2 = c1 + Direction.getHorizontalComponent(facing);
        // We transmit signs both directions at the same time, so we only want to get each pair of
        // creatures once. Anything 'below and to the right' is should be a pair that haven't shared
        // signs yet.
        if (r1 <= r2 && c1 <= c2)
        {
            if (_creatureGrid[r2][c2] != null && facing == Direction.flipDirection(_creatureGrid[r2][c2].getFacing()))
            {
                transmitSignsFromTo(r1, c1, r2, c2);
            }
        }
    }

    private void transmitSignsFromTo(int r1, int c1, int r2, int c2)
    {
        _creatureGrid[r1][c1].lookAtCreature(_creatureGrid[r2][c2]);
        _creatureGrid[r2][c2].lookAtCreature(_creatureGrid[r1][c1]);
    }

    private void letCreaturesPlan()
    {
        for (int r = 1; r < _height - 1; r++)
        {
            for (int c = 1; c < _width - 1; c++)
            {
                if (_creatureGrid[r][c] != null)
                {
                    _creatureGrid[r][c].planAction(this, r, c);
                }
            }
        }
    }

    private void executeCreaturePlans()
    {
        // Creatures act
        for (int r = 1; r < _height - 1; r++)
        {
            for (int c = 1; c < _width - 1; c++)
            {
                if (_creatureGrid[r][c] != null)
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
        else if (action == Action.MOVE && squareIsEmpty(facingR, facingC))
        {
            creature.executeAction(action);
            moveCreature(r, c, facing);
        }
        // Eating
        else if (action == Action.EAT && _itemGrid[facingR][facingC] == ItemType.FOOD)
        {
            creature.executeAction(action);
            removeObject(r, c, facing);
        }
        // Attempt to breed
        else if (action == Action.BREED
                // Make sure we're facing another creature
                && _creatureGrid[facingR][facingC] != null
                // And that creature is shares the same creature blueprint as us
                && _creatureGrid[facingR][facingC].getBlueprint() == creature.getBlueprint()
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
        else if (action == Action.BIRTH && squareIsEmpty(facingR, facingC))
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
                if (squareIsEmpty(r, c))
                {
                    double randomNumber = Rand.getInstance().getRandomPositiveDouble();
                    if (randomNumber < this._worldBlueprint.getFoodGenerationProbability())
                    {
                        this.setItem(ItemType.FOOD, r, c);
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

    private void moveCreature(int r, int c, Direction direction)
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

        _creatureGrid[r2][c2] = _creatureGrid[r1][c1];
        _creatureGrid[r1][c1] = null;
    }

    public void removeObject(int r, int c)
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

        _creatureGrid[r][c] = null;
        _itemGrid[r][c] = null;
        _terrainGrid[r][c] = null;
    }

    public LinkedList<AuditRecord> getAuditRecords()
    {
        LinkedList<AuditRecord> auditRecords = new LinkedList<>();
        for (int i = 0; i < this._auditRecords.length; i++)
        {
            auditRecords.add(_auditRecords[i]);
        }
        return auditRecords;
    }

    public WorldBlueprint getBlueprint()
    {
        return this._worldBlueprint;
    }

    public LinkedList<Creature> getCreatures()
    {
        LinkedList<Creature> allCreatures = new LinkedList<>();
        for (int r = 1; r < this._height - 1; r++)
        {
            for (int c = 1; c < this._width - 1; c++)
            {
                if (_creatureGrid[r][c] != null)
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

    public int getCreatureCount()
    {
        int count = 0;
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._creatureGrid[r][c] != null)
                {
                    count++;
                }
            }
        }
        return (count);
    }

    public int getItemCount()
    {
        int count = 0;
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._itemGrid[r][c] != null)
                {
                    count++;
                }
            }
        }
        return (count);
    }

    public int getTerrainCount()
    {
        int count = 0;
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._terrainGrid[r][c] != null)
                {
                    count++;
                }
            }
        }
        return (count);
    }

    public int getCount(CreatureBlueprint s)
    {
        int count = 0;
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._creatureGrid[r][c] != null && this._creatureGrid[r][c].getBlueprint().equals(s))
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

    public ItemType getItem(int r, int c)
    {
        return this._itemGrid[r][c];
    }

    public TerrainType getTerrain(int r, int c)
    {
        return this._terrainGrid[r][c];
    }

    public boolean squareIsEmpty(int r, int c)
    {
        return _creatureGrid[r][c] == null && _itemGrid[r][c] == null && _terrainGrid[r][c] == null;
    }

    private void addCreature(Creature creature, int r, int c)
    {
        creature.setID(this.getNewCreatureID());
        _creatureGrid[r][c] = creature;
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
            if (this.squareIsEmpty(r, c))
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

        this._creatureGrid = new Creature[height][width];
        this._itemGrid = new ItemType[height][width];
        this._terrainGrid = new TerrainType[height][width];
    }

    public void setTerrain(TerrainType terrainType, int r, int c)
    {
        this._terrainGrid[r][c] = terrainType;
    }

    public void setItem(ItemType itemType, int r, int c)
    {
        this._itemGrid[r][c] = itemType;
    }

    public String render(RenderCode code)
    {
        if (code == RenderCode.WORLD_MAP)
        {
            return (renderMap());
        }
        else if (code == RenderCode.PROCESSOR_WEIGHTS)
        {
            return (renderProcessorWeights());
        }
        else if (code == RenderCode.LIVE_CREATURE_LIST)
        {
            StringBuilder creatureOutput = new StringBuilder();
            for (int r = 0; r < this._height; r++)
            {
                for (int c = 0; c < this._width; c++)
                {
                    if (_creatureGrid[r][c] != null)
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
        worldOutput.append(this.getTerrainCount());
        HashMap<CreatureBlueprint, String> creatureBlueprintToGlyph = new HashMap<>();
        for (CreatureBlueprint s : this._worldBlueprint.getCreatureBlueprints())
        {
            creatureBlueprintToGlyph.put(s, glyphs[creatureBlueprintToGlyph.size()]);
            worldOutput.append(", ").append(creatureBlueprintToGlyph.get(s)).append("-creatures: ");
            worldOutput.append(this.getCount(s));
        }
        worldOutput.append(", Food: ");
        worldOutput.append(this.getItemCount());
        worldOutput.append('\n');
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (_terrainGrid[r][c] == TerrainType.WALL)
                {
                    worldOutput.append('口');
                }
                else if (_itemGrid[r][c] == ItemType.FOOD)
                {
                    worldOutput.append('一');
                }
                else if (_creatureGrid[r][c] != null)
                {
                    worldOutput.append(creatureBlueprintToGlyph.get(_creatureGrid[r][c].getBlueprint()));
                }
                else
                {
                    worldOutput.append('\u3000');
                }
            }
            worldOutput.append('\n');
        }
        return (worldOutput.toString());
    }

    private String renderProcessorWeights()
    {
        StringBuilder multiCreatureBlueprintOutput = new StringBuilder();
        for (CreatureBlueprint creatureBlueprint : this._worldBlueprint.getCreatureBlueprints())
        {
            multiCreatureBlueprintOutput.append(this.renderProcessorWeights(creatureBlueprint));
        }
        return multiCreatureBlueprintOutput.toString();
    }

    private String renderProcessorWeights(CreatureBlueprint s)
    {
        // Draw average processor
        // Draw creature readouts
        LinkedList<Processor> processors = new LinkedList<>();
        for (int r = 0; r < this._height; r++)
        {
            for (int c = 0; c < this._width; c++)
            {
                if (_creatureGrid[r][c] != null && _creatureGrid[r][c].getBlueprint().equals(s))
                {
                    processors.add(_creatureGrid[r][c].getProcessor());
                }
            }
        }
        if (processors.size() > 0)
        {
            return ProcessorType.render(processors.getFirst().getProcessorType(), processors);
        }
        return "";
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
