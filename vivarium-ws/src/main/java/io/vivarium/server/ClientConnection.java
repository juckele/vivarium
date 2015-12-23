/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.util.Optional;

import org.java_websocket.WebSocket;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;
import io.vivarium.util.concurrency.Stoppable;

public class ClientConnection implements Stoppable
{
    private final UUID _workerID;
    private Optional<WebSocket> _webSocket;

    public ClientConnection(UUID workerID, WebSocket webSocket)
    {
        this._workerID = workerID;
        this._webSocket = Optional.of(webSocket);
    }

    public synchronized void setWebSocket(WebSocket webSocket)
    {
        // Close the existing connection
        if (_webSocket.isPresent())
        {
            _webSocket.get().close(ClientConnectionManager.DUPLICATE_CONNECTION,
                    "Duplicate connection opened by client [" + _workerID + "]");
        }
        _webSocket = Optional.of(webSocket);
    }

    @Override
    public synchronized void stop()
    {
        if (_webSocket.isPresent())
        {
            _webSocket.get().close(ClientConnectionManager.SERVER_SHUTDOWN, "The server has been shut down.");
        }
    }

    public synchronized void socketClosed(WebSocket closedSocket)
    {
        Preconditions.checkArgument(_webSocket.get() == closedSocket);
        _webSocket = Optional.empty();
    }

    public UUID get_workerID()
    {
        return _workerID;
    }

    public synchronized Optional<WebSocket> getWebSocket()
    {
        return _webSocket;
    }
}
