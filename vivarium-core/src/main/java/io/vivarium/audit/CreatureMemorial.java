package io.vivarium.audit;

import io.vivarium.core.Species;
import io.vivarium.core.World;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CreatureMemorial extends AuditRecord
{
    private CreatureMemorial()
    {
        super();
    }

    public CreatureMemorial(Species species)
    {
        super(species);
    }

    @Override
    public void record(World world, int tick)
    {
        // TODO FILL
    }

    public static CreatureMemorial makeUninitialized()
    {
        return new CreatureMemorial();
    }

    public static CreatureMemorial makeWithSpecies(AuditFunction function, Species species)
    {
        return new CreatureMemorial(species);
    }
}
