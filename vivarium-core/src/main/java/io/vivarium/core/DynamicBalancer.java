package io.vivarium.core;

import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class DynamicBalancer extends VivariumObject
{
    static
    {
        ClassRegistry.getInstance().register(DynamicBalancer.class);
    }

    private DynamicBalancer()
    {
    }

    public static DynamicBalancer makeDefault()
    {
        DynamicBalancer b = new DynamicBalancer();
        return b;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
