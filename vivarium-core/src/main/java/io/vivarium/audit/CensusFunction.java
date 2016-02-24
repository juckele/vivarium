package io.vivarium.audit;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CensusFunction extends AuditFunction
{
    public CensusFunction()
    {
        super(AuditType.CENSUS);
    }
}
