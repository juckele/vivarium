package io.vivarium.net;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ClientWebSocketManager extends WebSocketClient implements OutboundNetworkConnection
{
    private final InboundNetworkListener _inboundListener;

    public ClientWebSocketManager(URI serverURI, InboundNetworkListener inboundListener)
    {
        super(serverURI);
        this._inboundListener = inboundListener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        _inboundListener.onOpen(this, handshakedata);
    }

    @Override
    public void onMessage(String message)
    {
        _inboundListener.onMessage(this, message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        _inboundListener.onClose(this, code, reason, remote);
    }

    @Override
    public void onError(Exception ex)
    {
        _inboundListener.onError(this, ex);
    }

}
