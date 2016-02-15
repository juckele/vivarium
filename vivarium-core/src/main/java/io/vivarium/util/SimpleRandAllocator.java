package io.vivarium.util;

public class SimpleRandAllocator implements RandAllocator
{
    private Rand _instance = new Rand();

    @Override
    public Rand getInstance()
    {
        return _instance;
    }

}
