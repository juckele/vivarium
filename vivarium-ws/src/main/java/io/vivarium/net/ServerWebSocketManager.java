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
        // TODO Auto-generated method stub

    }

    @Override
    public void onMessage(WebSocket webSocket, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(WebSocket webSocket, Exception ex)
    {
        // TODO Auto-generated method stub

    }

}
