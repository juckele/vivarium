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

    // Dependencies
    private final ClientConnectionFactory _clientConnectionFactory;

    // Internal state
    private Map<UUID, WebSocket> _workerIDToWebSocket = new HashMap<>();
    private Map<WebSocket, UUID> _webSocketToWorkerID = new HashMap<>();
    private Map<UUID, ClientConnection> _workerIDToConnection = new HashMap<>();

    public ClientConnectionManager(ClientConnectionFactory clientConnectionFactory)
    {
        this._clientConnectionFactory = clientConnectionFactory;
    }

    public synchronized ClientConnection registerWorker(UUID workerID, WebSocket workerSocket)
    {
        Preconditions.checkNotNull(workerID);
        Preconditions.checkNotNull(workerSocket);
        if (_workerIDToConnection.containsKey(workerID))
        {
            ClientConnection connection = _workerIDToConnection.get(workerID);
            connection.setWebSocket(workerSocket);

            // Update the uuid <-> socket maps
            internalDeregisterWorker(workerID, workerSocket);
            internalRegisterWorker(workerID, workerSocket);
        }
        else
        {
            ClientConnection connection = _clientConnectionFactory.make(workerID, workerSocket);
            _workerIDToConnection.put(workerID, connection);

            // Update the uuid <-> socket maps
            internalRegisterWorker(workerID, workerSocket);
        }
        return _workerIDToConnection.get(workerID);
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
            _workerIDToConnection.remove(uuid).stop();
        }
    }

    private void internalDeregisterWorker(UUID workerID, WebSocket workerSocket)
    {
        WebSocket existingConnection = _workerIDToWebSocket.remove(workerID);
        _webSocketToWorkerID.remove(existingConnection);
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
