package io.vivarium.core;

import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
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
    private int _safePopulation = 150;
    @SerializedParameter
    private double _minimumFoodGenerationProbability = 0.002;
    @SerializedParameter
    private int _minimumBreedingFoodRate = -800;

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
                double oldFoodGenerationProbability = blueprint.getFoodGenerationProbability();
                if (oldFoodGenerationProbability > _minimumFoodGenerationProbability)
                {
                    double newFoodGenerationProbability = Math.max(oldFoodGenerationProbability * 0.95,
                            _minimumFoodGenerationProbability);
                    blueprint.setFoodGenerationProbability(newFoodGenerationProbability);
                }
                else
                {
                    int oldBreedingFoodRate = species.getBreedingFoodRate();
                    if (oldBreedingFoodRate > _minimumBreedingFoodRate)
                    {
                        int newBreedingFoodRate = (int) Math.max(
                                Math.min(oldBreedingFoodRate * 1.01, oldBreedingFoodRate - 1),
                                _minimumBreedingFoodRate);
                        species.setBreedingFoodRate(newBreedingFoodRate);
                    }
                }
                _lastLowPopTickstamp = target.getTickCounter();
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
