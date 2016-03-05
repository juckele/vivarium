package io.vivarium.net;

/**
 * An object for sending messages over the network.
 */
public interface OutboundNetworkConnection
{
    void send(String text);

    void close();
}
