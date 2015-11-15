package io.vivarium.client;

import org.java_websocket.handshake.ServerHandshake;

public abstract class ClientTask
{
    public abstract void onOpen(Client client, ServerHandshake handshakedata);

    public abstract void onMessage(Client client, String message);

    public abstract void onClose(Client client, int code, String reason, boolean remote);

    public abstract void onError(Client client, Exception ex);

}