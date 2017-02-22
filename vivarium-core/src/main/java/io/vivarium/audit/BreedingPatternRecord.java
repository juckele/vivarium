package io.vivarium.audit;

import io.vivarium.core.Action;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.Direction;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class BreedingPatternRecord extends AuditRecord
{
    static
    {
        ClassRegistry.getInstance().register(BreedingPatternRecord.class);
    }

    // The action tally is a 4x dimensional counter of breeding actions.
    // index 1 generation (numeric, index is generation-1)
    // index 2 gender of 'first' partner (0 is male, 1 is female, 2 is pregnant female)
    // index 3 gender of 'second' partner (0 is male, 1 is female, 2 is pregnant female, 3 is none)
    // index 4 success (0 is failure, 1 is success)
    @SerializedParameter
    int[][][][] _tally = new int[16][3][4][2];

    protected BreedingPatternRecord()
    {
    }

    protected BreedingPatternRecord(CreatureBlueprint creatureBlueprint)
    {
        super(creatureBlueprint);
    }

    @Override
    public void record(GridWorld world, int tick)
    {
        for (int r = 0; r < world.getWorldHeight(); r++)
        {
            for (int c = 0; c < world.getWorldHeight(); c++)
            {
                Creature creature = world.getCreature(r, c);
                if (creature != null && creature.getAction() == Action.BREED)
                {
                    int targetR = r + Direction.getVerticalComponent(creature.getFacing());
                    int targetC = c + Direction.getHorizontalComponent(creature.getFacing());
                    Creature targetCreature = world.getCreature(targetR, targetC);
                    BreedingGender creatureGender = creature.getIsFemale()
                            ? (creature.getGestation() > 0 ? BreedingGender.PREGNANT : BreedingGender.FEMALE)
                            : BreedingGender.MALE;
                    BreedingGender targetCreatureGender;
                    if (targetCreature != null)
                    {
                        targetCreatureGender = targetCreature.getIsFemale()
                                ? (targetCreature.getGestation() > 0 ? BreedingGender.PREGNANT : BreedingGender.FEMALE)
                                : BreedingGender.MALE;
                    }
                    else
                    {
                        targetCreatureGender = BreedingGender.NONE;
                    }
                    addRecord((int) creature.getGeneration(), creatureGender, targetCreatureGender,
                            creature.wasSuccessful());
                }
            }
        }
    }

    private void addRecord(int generation, BreedingGender creature, BreedingGender targetCreature,
            boolean wasSuccessful)
    {
        while (generation > _tally.length)
        {
            resizeTally();
        }
        _tally[generation - 1][creature.index][targetCreature.index][wasSuccessful ? 1 : 0]++;
    }

    public int getMaximumGeneration()
    {
        int maximumGeneration = 0;
        generation: for (int i = 0; i < _tally.length; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                for (int k = 0; k < 4; k++)
                {
                    for (int l = 0; l < 2; l++)
                    {
                        if (_tally[i][j][k][l] > 0)
                        {
                            maximumGeneration = i;
                            continue generation;
                        }
                    }
                }
            }
        }
        return maximumGeneration + 1;
    }

    public int getRecord(int generation, BreedingGender creature, BreedingGender targetCreature, boolean wasSuccessful)
    {
        return _tally[generation - 1][creature.index][targetCreature.index][wasSuccessful ? 1 : 0];
    }

    private void resizeTally()
    {
        int[][][][] newTally = new int[_tally.length * 2][3][4][2];
        for (int i = 0; i < _tally.length; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                for (int k = 0; k < 4; k++)
                {
                    for (int l = 0; l < 2; l++)
                    {
                        newTally[i][j][k][l] = _tally[i][j][k][l];
                    }
                }
            }
        }
        _tally = newTally;
    }

    public static enum BreedingGender
    {
        MALE(0), FEMALE(1), PREGNANT(2), NONE(3);

        public final int index;

        BreedingGender(int index)
        {
            this.index = index;
        }
    }
}
