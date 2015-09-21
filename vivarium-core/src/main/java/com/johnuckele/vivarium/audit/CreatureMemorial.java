package com.johnuckele.vivarium.audit;

import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.ArchaicSerializedParameter;
import com.johnuckele.vivarium.serialization.SerializationCategory;

public class CreatureMemorial extends AuditRecord
{
    protected Species _trackedSpecies;

    private static final List<ArchaicSerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<ArchaicSerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS
                .add(new ArchaicSerializedParameter("trackedSpecies", Species.class, SerializationCategory.SPECIES));
    }

    private CreatureMemorial()
    {
        super();
    }

    public CreatureMemorial(Species species)
    {
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
