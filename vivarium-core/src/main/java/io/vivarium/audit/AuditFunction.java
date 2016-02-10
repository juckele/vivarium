/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.audit;

import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class AuditFunction extends VivariumObject
{
    @SerializedParameter
    protected AuditType _auditType;

    protected AuditFunction(AuditType auditType)
    {
        this._auditType = auditType;
    }

    public AuditType getAuditType()
    {
        return _auditType;
    }

    @Override
    public void finalizeSerialization()
    {
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_auditType == null) ? 0 : _auditType.hashCode());
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
        AuditFunction other = (AuditFunction) obj;
        if (_auditType != other._auditType)
        {
            return false;
        }
        return true;
    }
}
