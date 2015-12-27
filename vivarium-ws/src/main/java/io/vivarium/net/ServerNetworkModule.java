/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public class ServerNetworkModule extends NetworkModule
{
    public void sendMessage(UUID clientID, Message message)
    {
    }

    @Override
    void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
        // TODO Auto-generated method stub

    }
}
