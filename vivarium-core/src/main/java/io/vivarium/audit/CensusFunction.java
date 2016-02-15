package io.vivarium.audit;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class CensusFunction extends AuditFunction
{
    public CensusFunction()
    {
        super(AuditType.CENSUS);
    }
}
