/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

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
