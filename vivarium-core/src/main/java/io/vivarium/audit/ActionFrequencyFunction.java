package io.vivarium.audit;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class ActionFrequencyFunction extends AuditFunction
{
    public ActionFrequencyFunction()
    {
        super(AuditType.ACTION_FREQUENCY);
    }
}
