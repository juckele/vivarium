package com.johnuckele.vivarium.audit;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;

@SuppressWarnings("serial")
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
