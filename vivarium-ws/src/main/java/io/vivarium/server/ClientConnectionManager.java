package io.vivarium.server;

import java.util.HashMap;
import java.util.Map;

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
        _webSocketToWorkerID.clear();
        for (UUID uuid : _workerIDToConnection.keySet())
        {
            _workerIDToConnection.remove(uuid).stop();
        }
    }

    private void internalDeregisterWorker(UUID workerID, WebSocket workerSocket)
    {
        _webSocketToWorkerID.remove(workerSocket);
    }

    private void internalRegisterWorker(UUID workerID, WebSocket workerSocket)
    {
        _webSocketToWorkerID.put(workerSocket, workerID);
    }

    public synchronized void socketClosed(WebSocket closedSocket)
    {
        Preconditions.checkNotNull(closedSocket);
        if (_webSocketToWorkerID.containsKey(closedSocket))
        {
            UUID workerID = _webSocketToWorkerID.remove(closedSocket);
            _workerIDToConnection.get(workerID).socketClosed(closedSocket);
        }
    }

    public synchronized ClientConnection getConnectionForWorker(UUID workerID)
    {
        Preconditions.checkNotNull(workerID);
        if (_workerIDToConnection.containsKey(workerID))
        {
            return _workerIDToConnection.get(workerID);
        }
        else
        {
            throw new IllegalStateException("Worker " + workerID + " is not known.");
        }
    }
}
