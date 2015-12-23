/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import org.java_websocket.WebSocket;

import io.vivarium.util.UUID;
import io.vivarium.util.concurrency.Stoppable;

public class ClientConnection implements Stoppable
{
    private final UUID _workerID;
    private WebSocket _webSocket;

    public ClientConnection(UUID workerID, WebSocket webSocket)
    {
        super();
        this._workerID = workerID;
        this._webSocket = webSocket;
    }

    public synchronized void setWebSocket(WebSocket webSocket)
    {
        // Close the existing connection
        if (_webSocket != null)
        {
            _webSocket.close(ClientConnectionManager.DUPLICATE_CONNECTION,
                    "Duplicate connection opened by client [" + _workerID + "]");
        }
        _webSocket = webSocket;
    }

    @Override
    public void stop()
    {
        _webSocket.close(ClientConnectionManager.SERVER_SHUTDOWN, "The server has been shut down.");
    }
}
