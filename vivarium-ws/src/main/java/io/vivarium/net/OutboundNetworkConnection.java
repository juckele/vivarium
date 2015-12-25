/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

/**
 * An object for sending messages over the network.
 */
public interface OutboundNetworkConnection
{
    public void send(String text);

    public void close();
}
