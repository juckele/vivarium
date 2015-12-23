/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import static org.mockito.Mockito.mock;

import org.java_websocket.WebSocket;
import org.junit.Test;

import com.johnuckele.vtest.Tester;

import io.vivarium.util.UUID;

public class ClientConnectionManagerTest
{
    @Test
    public void testRegisterWorker()
    {
        // Create CCM
        ClientConnectionFactory factory = new ClientConnectionFactory();
        ClientConnectionManager manager = new ClientConnectionManager(factory);

        // Register a worker
        UUID workerID = UUID.randomUUID();
        WebSocket workerSocket = mock(WebSocket.class);
        manager.registerWorker(workerID, workerSocket);

        // Get the worker socket
        ClientConnection fetchedConnection = manager.getConnectionForWorker(workerID);

        Tester.isTrue("original socket and fetched socket are the same",
                workerSocket == fetchedConnection.getWebSocket().get());

        // Register a second worker
        UUID workerID2 = UUID.randomUUID();
        WebSocket workerSocket2 = mock(WebSocket.class);
        manager.registerWorker(workerID2, workerSocket2);

        // Get the second worker socket
        ClientConnection fetchedConnection2 = manager.getConnectionForWorker(workerID2);
        Tester.isFalse("Fetched sockets from different workers are not the same",
                fetchedConnection.getWebSocket().get() == fetchedConnection2.getWebSocket().get());
    }

}
