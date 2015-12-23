/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.java_websocket.WebSocket;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;
import io.vivarium.util.concurrency.StartableStoppable;

public class ClientConnectionManager implements StartableStoppable
{
    public static final int DUPLICATE_CONNECTION = 2127;
    public static final int SERVER_SHUTDOWN = 2128;

    private Map<UUID, WebSocket> _workerIDToWebSocket = new HashMap<>();
    private Map<WebSocket, UUID> _webSocketToWorkerID = new HashMap<>();

    public synchronized void registerWorker(UUID workerID, WebSocket workerSocket)
    {
        Preconditions.checkNotNull(workerID);
        Preconditions.checkNotNull(workerSocket);
        if (_workerIDToWebSocket.containsKey(workerID))
        {
            // Remove the existing connection from the maps
            WebSocket existingConnection = _workerIDToWebSocket.remove(workerID);
            _webSocketToWorkerID.remove(existingConnection);

            // Place a new connection into the maps
            internalRegisterWorker(workerID, workerSocket);

            // Close the existing connection
            existingConnection.close(DUPLICATE_CONNECTION, "Duplicate connection opened by client [" + workerID + "]");
        }
        else
        {
            // Place a new connection into the maps
            internalRegisterWorker(workerID, workerSocket);
        }
    }

    @Override
    public void start()
    {
        // Nothing required to start
    }

    @Override
    public synchronized void stop()
    {
        for (Entry<UUID, WebSocket> entry : _workerIDToWebSocket.entrySet())
        {
            UUID uuid = entry.getKey();
            WebSocket socket = entry.getValue();
            _workerIDToWebSocket.remove(uuid);
            _webSocketToWorkerID.remove(socket);
            socket.close(SERVER_SHUTDOWN, "The server has been shut down.");
        }
    }

    private void internalRegisterWorker(UUID workerID, WebSocket workerSocket)
    {
        _workerIDToWebSocket.put(workerID, workerSocket);
        _webSocketToWorkerID.put(workerSocket, workerID);
    }

    public synchronized Optional<UUID> socketClosed(WebSocket closedSocket)
    {
        Preconditions.checkNotNull(closedSocket);
        if (_webSocketToWorkerID.containsKey(closedSocket))
        {
            UUID workerID = _webSocketToWorkerID.remove(closedSocket);
            _workerIDToWebSocket.remove(workerID);
            return Optional.of(workerID);
        }
        else
        {
            return Optional.empty();
        }
    }

    public synchronized Optional<WebSocket> getSocketForWorker(UUID workerID)
    {
        Preconditions.checkNotNull(workerID);
        if (_workerIDToWebSocket.containsKey(workerID))
        {
            return Optional.of(_workerIDToWebSocket.get(workerID));
        }
        else
        {
            return Optional.empty();
        }
    }
}
