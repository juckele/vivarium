package io.vivarium.audit;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CreatureMemorial extends AuditRecord
{
    private CreatureMemorial()
    {
        super();
    }

    public CreatureMemorial(CreatureBlueprint species)
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

    public static CreatureMemorial makeWithSpecies(AuditBlueprint function, CreatureBlueprint species)
    {
        return new CreatureMemorial(species);
    }
}
