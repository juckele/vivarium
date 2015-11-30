/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.java_websocket.WebSocket;

import com.google.common.base.Preconditions;

import io.vivarium.util.UUID;

public class ClientConnectionManager
{
    private static final int DUPLICATE_CONNECTION = 2127;
    private Map<UUID, WebSocket> workerIDToConnection = new HashMap<UUID, WebSocket>();
    private Map<WebSocket, UUID> connectionToWorkerID = new HashMap<WebSocket, UUID>();

    public synchronized void registerWorker(UUID workerID, WebSocket workerSocket)
    {
        Preconditions.checkNotNull(workerID);
        Preconditions.checkNotNull(workerSocket);
        if (workerIDToConnection.containsKey(workerID))
        {
            // Remove the existing connection from the maps
            WebSocket existingConnection = workerIDToConnection.remove(workerID);
            connectionToWorkerID.remove(existingConnection);

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

    private void internalRegisterWorker(UUID workerID, WebSocket workerSocket)
    {
        workerIDToConnection.put(workerID, workerSocket);
        connectionToWorkerID.put(workerSocket, workerID);
    }

    public synchronized Optional<UUID> socketClosed(WebSocket closedSocket)
    {
        Preconditions.checkNotNull(closedSocket);
        if (connectionToWorkerID.containsKey(closedSocket))
        {
            UUID workerID = connectionToWorkerID.remove(closedSocket);
            workerIDToConnection.remove(workerID);
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
        if (workerIDToConnection.containsKey(workerID))
        {
            return Optional.of(workerIDToConnection.get(workerID));
        }
        else
        {
            return Optional.empty();
        }
    }
}
