package io.vivarium.core.processor;

import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Multiplexer extends VivariumObject
{
    @Override
    public void finalizeSerialization()
    {
    }
}
