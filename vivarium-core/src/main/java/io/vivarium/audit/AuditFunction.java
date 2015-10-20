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
        // TODO Auto-generated method stub

    }

}
