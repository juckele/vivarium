package io.vivarium.util.concurrency;

import io.vivarium.util.Rand;
import io.vivarium.util.RandAllocator;

public class ThreadRandAllocator implements RandAllocator
{
    ThreadLocal<Rand> _instances = new ThreadLocal<Rand>()
    {
        @Override
        protected Rand initialValue()
        {
            return new Rand();
        }
    };

    @Override
    public Rand getInstance()
    {
        return _instances.get();
    }

}
