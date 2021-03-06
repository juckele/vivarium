package io.vivarium.audit;

import io.vivarium.core.Action;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class ActionFrequencyRecord extends AuditRecord
{
    static
    {
        ClassRegistry.getInstance().register(ActionFrequencyRecord.class);
    }

    // The action tally is a 5x dimensional counter of actions.
    // index 1 generation (numeric, index is generation-1)
    // index 2 gender (0 is male, 1 is female)
    // index 3 gestation (0 is not-pregnant, 2 is pregnant)
    // index 4 action (indexes are based on Action.convertActionToInteger)
    // index 5 success (0 is failure, 1 is success)
    @SerializedParameter
    int[][][][][] _tally = new int[16][2][2][Action.values().length][2];

    protected ActionFrequencyRecord()
    {
    }

    protected ActionFrequencyRecord(CreatureBlueprint creatureBlueprint)
    {
        super(creatureBlueprint);
    }

    @Override
    public void record(World world, int tick)
    {
        for (Creature creature : world.getCreatures())
        {
            if (creature != null && creature.getBlueprint() == this._trackedCreatureBlueprint)
            {
                addRecord((int) creature.getGeneration(), creature.getIsFemale(), creature.getGestation() > 0,
                        creature.getAction(), creature.wasSuccessful());
            }
        }
    }

    private void addRecord(int generation, boolean isFemale, boolean isPregnant, Action action, boolean wasSuccessful)
    {
        while (generation > _tally.length)
        {
            resizeTally();
        }
        _tally[generation - 1][isFemale ? 1 : 0][isPregnant ? 1 : 0][Action
                .convertActionToInteger(action)][wasSuccessful ? 1 : 0]++;
    }

    public int getMaximumGeneration()
    {
        int maximumGeneration = 0;
        generation: for (int i = 0; i < _tally.length; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                for (int k = 0; k < 2; k++)
                {
                    for (int l = 0; l < Action.values().length; l++)
                    {
                        for (int m = 0; m < 2; m++)
                        {
                            if (_tally[i][j][k][l][m] > 0)
                            {
                                maximumGeneration = i;
                                continue generation;
                            }
                        }
                    }
                }
            }
        }
        return maximumGeneration + 1;
    }

    public int getRecord(int generation, boolean isFemale, boolean isPregnant, Action action, boolean wasSuccessful)
    {
        return _tally[generation - 1][isFemale ? 1 : 0][isPregnant ? 1 : 0][Action
                .convertActionToInteger(action)][wasSuccessful ? 1 : 0];
    }

    private void resizeTally()
    {
        int[][][][][] newTally = new int[_tally.length * 2][2][2][Action.values().length][2];
        for (int i = 0; i < _tally.length; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                for (int k = 0; k < 2; k++)
                {
                    for (int l = 0; l < Action.values().length; l++)
                    {
                        for (int m = 0; m < 2; m++)
                        {
                            newTally[i][j][k][l][m] = _tally[i][j][k][l][m];
                        }
                    }
                }
            }
        }
        _tally = newTally;
    }
}
