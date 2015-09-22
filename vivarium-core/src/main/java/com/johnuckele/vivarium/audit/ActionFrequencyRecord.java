package com.johnuckele.vivarium.audit;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;

public class ActionFrequencyRecord extends AuditRecord
{

    private ActionFrequencyRecord()
    {
    }

    protected ActionFrequencyRecord(Species species)
    {
    }

    @Override
    public void record(World world, int tick)
    {
        // TODO FILL
    }

    public static ActionFrequencyRecord makeUninitialized()
    {
        return new ActionFrequencyRecord();
    }

    public static ActionFrequencyRecord makeWithSpecies(AuditFunction function, Species species)
    {
        return new ActionFrequencyRecord(species);
    }
}
