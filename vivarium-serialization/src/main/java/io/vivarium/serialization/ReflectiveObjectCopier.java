package io.vivarium.serialization;

public class ReflectiveObjectCopier implements VivariumObjectCopier
{
    @Override
    public <T extends VivariumObject> T copyObject(T object)
    {
        T copy = new SerializationEngine().makeCopy(object);
        return copy;
    }
}
