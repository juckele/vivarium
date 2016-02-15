package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public class ServerNetworkModule extends NetworkModule
{
    public ServerNetworkModule(InboundNetworkListener inboundListener, ObjectMapper messageMapper)
    {
        super(inboundListener, messageMapper);
    }

    public void sendMessage(UUID clientID, Message message)
    {
    }

    @Override
    void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
        // TODO Auto-generated method stub

    }
}
