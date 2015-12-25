/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import io.vivarium.net.messages.Message;

public class NetworkModule
{
    public <T extends Message> void addMessageListener(MessageListener<T> listener, Class<T> messageClazz)
    {
    }

    protected void sendMessage(OutboundNetworkConnection outbound, String text)
    {

    }

    void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
    }
}
