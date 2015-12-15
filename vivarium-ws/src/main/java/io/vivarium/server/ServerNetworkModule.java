package io.vivarium.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ServerNetworkModule extends WebSocketServer
{
    private final MessageRouter _router;

    public ServerNetworkModule(InetSocketAddress port, MessageRouter router)
    {
        super(port);
        _router = router;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        _router.onOpen(conn, handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        _router.onClose(conn, code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        _router.onMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        _router.onError(conn, ex);
    }

}
