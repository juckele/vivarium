package io.vivarium.serialization;

public interface VivariumObjectCopier
{
    <T extends VivariumObject> T copyObject(T object);
}
