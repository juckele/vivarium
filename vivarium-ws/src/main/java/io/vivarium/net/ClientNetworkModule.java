/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.messages.Message;

public class ClientNetworkModule extends NetworkModule
{
    private OutboundNetworkConnection _outboundConnection = null;

    public ClientNetworkModule(InboundNetworkListener inboundListener, ObjectMapper _messageMapper)
    {
        super(inboundListener, _messageMapper);
        // TODO Auto-generated constructor stub
    }

    synchronized public void sendMessage(Message message)
    {
        super.sendMessage(_outboundConnection, message);
    }

    @Override
    synchronized void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
        _outboundConnection = outboundNetworkConnection;
    }
}
