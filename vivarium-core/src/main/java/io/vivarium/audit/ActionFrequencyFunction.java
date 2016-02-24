package io.vivarium.audit;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class ActionFrequencyFunction extends AuditFunction
{
    public ActionFrequencyFunction()
    {
        super(AuditType.ACTION_FREQUENCY);
    }
}
