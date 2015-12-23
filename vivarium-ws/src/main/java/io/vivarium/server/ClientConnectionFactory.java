/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import org.java_websocket.WebSocket;

import io.vivarium.util.UUID;

public class ClientConnectionFactory
{
    public ClientConnection make(UUID workerID, WebSocket webSocket)
    {
        return new ClientConnection(workerID, webSocket);
    }
}
