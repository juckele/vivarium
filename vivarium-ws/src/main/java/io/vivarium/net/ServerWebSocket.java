package io.vivarium.net;

import org.java_websocket.WebSocket;

/**
 * Wraps a WebSocket so that is can be treated as an OutboundNetworkConnection.
 */
public class ServerWebSocket implements OutboundNetworkConnection
{
    private final WebSocket _webSocket;

    public ServerWebSocket(WebSocket webSocket)
    {
        this._webSocket = webSocket;
    }

    @Override
    public void send(String text)
    {
        _webSocket.send(text);
    }

    @Override
    public void close()
    {
        _webSocket.close();
    }

}
