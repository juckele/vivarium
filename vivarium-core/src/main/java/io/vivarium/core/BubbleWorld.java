package io.vivarium.core;

import java.util.ArrayList;
import java.util.List;

import io.vivarium.core.bubble.BubblePosition;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class BubbleWorld extends World
{
    static
    {
        ClassRegistry.getInstance().register(BubbleWorld.class);
    }

    @SerializedParameter
    private double _width;
    @SerializedParameter
    private double _height;

    @SerializedParameter
    protected List<Creature> _creatures;
    @SerializedParameter
    protected List<BubblePosition> _creaturePositions;

    @SerializedParameter
    private BubbleWorldBlueprint _bubbleWorldBlueprint;

    // Private constructor for deserialization
    @SuppressWarnings("unused")
    private BubbleWorld()
    {
    }

    public BubbleWorld(BubbleWorldBlueprint bubbleWorldBlueprint)
    {
        super(bubbleWorldBlueprint);

        // Store the world blueprint
        this._bubbleWorldBlueprint = bubbleWorldBlueprint;

        _creatures = new ArrayList<Creature>();
        _creaturePositions = new ArrayList<BubblePosition>();

        initialize();
    }

    @Override
    protected void executeCreaturePlans()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCount(CreatureBlueprint s)
    {
        int speciesCount = 0;
        for (Creature creature : _creatures)
        {
            if (creature.getCreatureBlueprint() == s)
            {
                speciesCount++;
            }
        }
        return speciesCount;
    }

    @Override
    public int getCreatureCount()
    {
        return _creatures.size();
    }

    @Override
    public List<Creature> getCreatures()
    {
        return _creatures;
    }

    public List<BubblePosition> getCreaturePositions()
    {
        return _creaturePositions;
    }

    @Override
    protected void letCreaturesPlan()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void populatateWorld()
    {
        int initialCreatureCount = this._bubbleWorldBlueprint.getInitialCreaturePopulation();
        ArrayList<CreatureBlueprint> creatureBlueprints = _bubbleWorldBlueprint._creatureBlueprints;
        for (int i = 0; i < initialCreatureCount; i++)
        {
            Creature creature = new Creature(creatureBlueprints.get(0));
            BubblePosition position = new BubblePosition(0, 30, 0, 30, 0, Math.PI * 2, 1, 1);
            addCreature(creature, position);
        }
    }

    private void addCreature(Creature creature, BubblePosition position)
    {
        creature.setID(this.getNewCreatureID());
        _creatures.add(creature);
        _creaturePositions.add(position);
    }

    @Override
    protected void spawnFood()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void tickCreatures()
    {
        for (int i = 0; i < _creatures.size(); i++)
        {
            Creature creature = _creatures.get(i);
            BubblePosition creaturePosition = _creaturePositions.get(i);
            creature.setAge(creature.getAge() + 1);
            creaturePosition.setHeading(creaturePosition.getHeading() + (creature.getIsFemale() ? 1 : -1));
        }
        // TODO Auto-generated method stub

    }

    @Override
    protected void tickPhysics()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void tickTerrain()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void transmitSigns()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void transmitSounds()
    {
        // TODO Auto-generated method stub

    }

    public int getItemCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
