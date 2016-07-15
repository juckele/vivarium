package io.vivarium.util.concurrency;

@FunctionalInterface
public interface VoidFunction
{
    /**
     * Execute the VoidFunction, side effects should be assumed.
     */
    void execute();
}
