package io.vivarium.server;

@FunctionalInterface
public interface VoidFunction
{
    /**
     * Execute the VoidFunction, side effects should be assumed.
     */
    void execute();
}
