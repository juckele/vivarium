/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.audit;

import io.vivarium.core.Species;
import io.vivarium.core.World;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class AuditRecord extends VivariumObject
{
    @SerializedParameter
    protected Species _trackedSpecies;

    protected AuditRecord()
    {
    }

    protected AuditRecord(Species species)
    {
        _trackedSpecies = species;
    }

    /**
     * Inspect a world, recording any information required by the audit record
     *
     * @param inputs
     * @return outputsS
     */
    public abstract void record(World world, int tick);

    @Override
    public void finalizeSerialization()
    {
        // TODO Auto-generated method stub

    }

}