package io.vivarium.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import io.vivarium.audit.AuditRecord;
import io.vivarium.core.GridWorldPopulator.EntityType;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class GridWorld extends World
{
    static
    {
        ClassRegistry.getInstance().register(GridWorld.class);
    }

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
    private GridWorldBlueprint _gridWorldBlueprint;
    @SerializedParameter
    private DynamicBalancer _balancer;

    // Private constructor for deserialization
    @SuppressWarnings("unused")
    private GridWorld()
    {
    }

    public GridWorld(GridWorldBlueprint gridWorldBlueprint)
    {
        super(gridWorldBlueprint);

        // Store the world blueprint
        this._gridWorldBlueprint = gridWorldBlueprint;

        // Size the world
        this.setWorldDimensions(gridWorldBlueprint.getWidth(), gridWorldBlueprint.getHeight());

        initialize();
    }

    public DynamicBalancer getDynamicBalancer()
    {
        return this._balancer;
    }

    public void setDynamicBalancer(DynamicBalancer balancer)
    {
        this._balancer = balancer;
    }

    public int getHeight()
    {
        return _height;
    }

    public int getWidth()
    {
        return _width;
    }

    @Override
    protected void populatateWorld()
    {
        GridWorldPopulator populator = new GridWorldPopulator();
        populator.setCreatureBlueprints(_gridWorldBlueprint.getCreatureBlueprints());
        populator.setWallProbability(_gridWorldBlueprint.getInitialWallGenerationProbability());
        populator.setFoodGeneratorProbability(_gridWorldBlueprint.getFoodGeneratorProbability());
        populator.setFoodProbability(_gridWorldBlueprint.getInitialFoodGenerationProbability());
        populator.setFlamethrowerProbability(_gridWorldBlueprint.getFlamethrowerProbability());
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

                        setTerrain(populator.getTerrainType(), r, c);
                    }
                }
            }
        }
    }

    @Override
    protected void tickCreatures()
    {
        for (int r = 1; r < _height - 1; r++)
        {
            for (int c = 1; c < _width - 1; c++)
            {
                if (_creatureGrid[r][c] != null)
                {
                    int flameAmount = _terrainGrid[r][c] == TerrainType.FLAME ? 1 : 0;
                    _creatureGrid[r][c].tick(flameAmount);
                }
            }
        }
    }

    @Override
    protected void transmitSounds()
    {
        if (this._gridWorldBlueprint.getSoundEnabled())
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

    @Override
    protected void transmitSigns()
    {
        if (this._gridWorldBlueprint.getSignEnabled())
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

    @Override
    protected void letCreaturesPlan()
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

    @Override
    protected void executeCreaturePlans()
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
        else if (action == Action.MOVE && squareIsPathable(facingR, facingC))
        {
            creature.executeAction(action);
            moveCreature(r, c, facing);
        }
        // Eating
        else if (action == Action.EAT && _itemGrid[r][c] == ItemType.FOOD)
        {
            creature.executeAction(action);
            removeFood(r, c);
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
        // Fighting
        else if (action == Action.FIGHT
                // Make sure we're facing another creature
                && _creatureGrid[facingR][facingC] != null)
        {
            creature.executeAction(action, _creatureGrid[facingR][facingC]);
        }
        // Giving Birth
        else if (action == Action.BIRTH && squareIsPathable(facingR, facingC))
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

    @Override
    protected void tickTerrain()
    {
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (this._terrainGrid[r][c] == TerrainType.FOOD_GENERATOR)
                {
                    if (squareIsFoodable(r + 1, c))
                    {
                        this.setItem(ItemType.FOOD, r + 1, c);
                    }
                    if (squareIsFoodable(r - 1, c))
                    {
                        this.setItem(ItemType.FOOD, r - 1, c);
                    }
                    if (squareIsFoodable(r, c + 1))
                    {
                        this.setItem(ItemType.FOOD, r, c + 1);
                    }
                    if (squareIsFoodable(r, c - 1))
                    {
                        this.setItem(ItemType.FOOD, r, c - 1);
                    }
                }

                if (this._terrainGrid[r][c] == TerrainType.FLAMETHROWER)
                {
                    if (squareIsFlamable(r + 1, c))
                    {
                        this.setTerrain(TerrainType.FLAME, r + 1, c);
                    }
                    if (squareIsFlamable(r - 1, c))
                    {
                        this.setTerrain(TerrainType.FLAME, r - 1, c);
                    }
                    if (squareIsFlamable(r, c + 1))
                    {
                        this.setTerrain(TerrainType.FLAME, r, c + 1);
                    }
                    if (squareIsFlamable(r, c - 1))
                    {
                        this.setTerrain(TerrainType.FLAME, r, c - 1);
                    }
                }
            }
        }
    }

    @Override
    protected void spawnFood()
    {
        // Generate food at a given rate
        for (int r = 0; r < _height; r++)
        {
            for (int c = 0; c < _width; c++)
            {
                if (squareIsFoodable(r, c))
                {
                    double randomNumber = Rand.getInstance().getRandomPositiveDouble();
                    if (randomNumber < this._gridWorldBlueprint.getFoodGenerationProbability())
                    {
                        this.setItem(ItemType.FOOD, r, c);
                    }
                }
            }
        }
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

    public void removeCreature(int r, int c)
    {
        _creatureGrid[r][c] = null;

    }

    public void removeFood(int r, int c)
    {
        _itemGrid[r][c] = null;
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

    @Override
    public void tick()
    {
        super.tick();

        // Run balancer if it's present
        if (this._balancer != null)
        {
            _balancer.balance(this);
        }
    }

    public GridWorldBlueprint getGridWorldBlueprint()
    {
        return this._gridWorldBlueprint;
    }

    @Override
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

    @Override
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

    @Override
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

    public boolean squareIsPathable(int r, int c)
    {
        return _creatureGrid[r][c] == null && TerrainType.isPathable(_terrainGrid[r][c]);
    }

    public boolean squareIsFoodable(int r, int c)
    {
        return _itemGrid[r][c] == null && _terrainGrid[r][c] == null;
    }

    public boolean squareIsFlamable(int r, int c)
    {
        return _terrainGrid[r][c] == null;
    }

    private void addCreature(Creature creature, int r, int c)
    {
        creature.setID(this.getNewCreatureID());
        _creatureGrid[r][c] = creature;
    }

    private void killCreature(int r, int c)
    {
        removeCreature(r, c);
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

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
