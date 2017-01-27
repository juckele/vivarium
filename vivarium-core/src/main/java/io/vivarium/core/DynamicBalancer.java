package io.vivarium.core;

import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class DynamicBalancer extends VivariumObject
{
    static
    {
        ClassRegistry.getInstance().register(DynamicBalancer.class);
    }

    @SerializedParameter
    private int _lastLowPopTickstamp = 0;
    @SerializedParameter
    private int _timePeriod = 2_000;
    @SerializedParameter
    private int _safePopulation = 300;
    @SerializedParameter
    private double _minimumFoodGenerationProbability = 0.002;
    @SerializedParameter
    private int _minimumBreedingFoodRate = -1500;

    private DynamicBalancer()
    {
    }

    public void balance(World target)
    {
        WorldBlueprint blueprint = target.getBlueprint();
        CreatureBlueprint species = blueprint.getCreatureBlueprints().get(0);
        if (target.getCreatureCount() > _safePopulation)
        {
            if (target.getTickCounter() - _timePeriod > _lastLowPopTickstamp)
            {
                double traitSelector = Rand.getInstance().getRandomPositiveDouble();
                if (traitSelector < 0.01)
                {
                    // Try to lower food spawn rate
                    double oldFoodGenerationProbability = blueprint.getFoodGenerationProbability();
                    if (oldFoodGenerationProbability > _minimumFoodGenerationProbability)
                    {
                        // Lower food spawn rate
                        double newFoodGenerationProbability = Math.max(oldFoodGenerationProbability * 0.95,
                                _minimumFoodGenerationProbability);
                        blueprint.setFoodGenerationProbability(newFoodGenerationProbability);

                        // Reset the low tick population tracker
                        _lastLowPopTickstamp = target.getTickCounter();
                    }
                }
                else if (traitSelector < 1)
                {
                    // Try to raise breeding cost
                    int oldBreedingFoodRate = species.getBreedingFoodRate();
                    if (oldBreedingFoodRate > _minimumBreedingFoodRate)
                    {
                        // Raise breeding cost
                        int newBreedingFoodRate = (int) Math.max(
                                Math.min(oldBreedingFoodRate * 1.01, oldBreedingFoodRate - 1),
                                _minimumBreedingFoodRate);
                        species.setBreedingFoodRate(newBreedingFoodRate);

                        // Reset the low tick population tracker
                        _lastLowPopTickstamp = target.getTickCounter();
                    }
                }
                else
                {
                }
            }
        }
        else
        {
            _lastLowPopTickstamp = target.getTickCounter();
        }
    }

    public static DynamicBalancer makeDefault()
    {
        DynamicBalancer b = new DynamicBalancer();
        return b;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
