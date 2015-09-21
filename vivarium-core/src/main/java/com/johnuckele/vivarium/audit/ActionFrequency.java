package com.johnuckele.vivarium.audit;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;

public class ActionFrequency extends AuditRecord
{

    private ActionFrequency()
    {
    }

    protected ActionFrequency(Species species)
    {
    }

    @Override
    public void record(World world, int tick)
    {
        // TODO FILL
    }

    public static ActionFrequency makeUninitialized()
    {
        return new ActionFrequency();
    }

    public static ActionFrequency makeWithSpecies(AuditFunction function, Species species)
    {
        return new ActionFrequency(species);
    }
}
