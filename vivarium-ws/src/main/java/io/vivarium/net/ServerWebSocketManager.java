/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ServerWebSocketManager extends WebSocketServer
{
    private final InboundNetworkListener _inboundListener;

    public ServerWebSocketManager(InetSocketAddress address, InboundNetworkListener inboundListener)
    {
        super(address);
        this._inboundListener = inboundListener;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake handshake)
    {
        _inboundListener.onOpen(new ServerWebSocket(webSocket), handshake);
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote)
    {
        _inboundListener.onClose(new ServerWebSocket(webSocket), code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message)
    {
        _inboundListener.onMessage(new ServerWebSocket(webSocket), message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception ex)
    {
        _inboundListener.onError(new ServerWebSocket(webSocket), ex);
    }

}
