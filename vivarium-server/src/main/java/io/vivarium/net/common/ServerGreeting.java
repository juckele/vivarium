/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common;

public class ServerGreeting extends Message
{
    public final static String TYPE = "SERVER_GREETING";

    public final int networkProtocol = 1;

    @Override
    protected String getType()
    {
        return TYPE;
    }
}
