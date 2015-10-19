package io.vivarium.net.common;

public abstract class Message
{
    public final String type;

    protected Message()
    {
        type = getType();
    }

    protected abstract String getType();
}
