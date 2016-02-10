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
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_trackedSpecies == null) ? 0 : _trackedSpecies.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AuditRecord other = (AuditRecord) obj;
        if (_trackedSpecies == null)
        {
            if (other._trackedSpecies != null)
            {
                return false;
            }
        }
        else if (!_trackedSpecies.equals(other._trackedSpecies))
        {
            return false;
        }
        return true;
    }

}