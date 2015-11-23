/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import org.java_websocket.handshake.ServerHandshake;

public class NullTask extends ClientTask
{
    @Override
    public void onOpen(Client client, ServerHandshake handshakedata)
    {
    }

    @Override
    public void onMessage(Client client, String message)
    {
    }

    @Override
    public void onClose(Client client, int code, String reason, boolean remote)
    {
    }

    @Override
    public void onError(Client client, Exception ex)
    {
    }
}
