package io.vivarium.server;

public interface StartableStoppable
{
    void start() throws Exception;

    void stop() throws Exception;
}
