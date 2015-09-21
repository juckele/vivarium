package com.johnuckele.vivarium.audit;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;

public abstract class AuditRecord implements MapSerializer
{
    @SerializedParameter
    protected Species _trackedSpecies;

    protected AuditRecord()
    {

    }

    /**
     * Inspect a world, recording any information required by the audit record
     *
     * @param inputs
     * @return outputsS
     */
    public abstract void record(World world, int tick);

    public static AuditRecord makeCopy(AuditRecord original)
    {
        return (AuditRecord) new SerializationEngine().makeCopy(original);
    }

    @Override
    public void finalizeSerialization()
    {
        // TODO Auto-generated method stub

    }

}