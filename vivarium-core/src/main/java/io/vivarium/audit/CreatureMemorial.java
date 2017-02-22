package io.vivarium.audit;

import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.GridWorld;
import io.vivarium.serialization.ClassRegistry;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CreatureMemorial extends AuditRecord
{
    static
    {
        ClassRegistry.getInstance().register(CreatureMemorial.class);
    }

    private CreatureMemorial()
    {
        super();
    }

    public CreatureMemorial(CreatureBlueprint creatureBlueprint)
    {
        super(creatureBlueprint);
    }

    @Override
    public void record(GridWorld world, int tick)
    {
        // TODO FILL
    }

    public static CreatureMemorial makeUninitialized()
    {
        return new CreatureMemorial();
    }
}
