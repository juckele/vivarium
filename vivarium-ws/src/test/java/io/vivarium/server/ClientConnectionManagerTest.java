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
        ClientConnectionManager manager = new ClientConnectionManager(mock(ClientConnectionFactory.class));

        // Register a worker
        UUID workerID = UUID.randomUUID();
        WebSocket workerSocket = mock(WebSocket.class);
        manager.registerWorker(workerID, workerSocket);

        // Get the worker socket
        WebSocket fetchedWorkerSocket = manager.getSocketForWorker(workerID).get();

        Tester.isTrue("original socket and fetched socket are the same", workerSocket == fetchedWorkerSocket);

        // Register a second worker
        UUID workerID2 = UUID.randomUUID();
        WebSocket workerSocket2 = mock(WebSocket.class);
        manager.registerWorker(workerID2, workerSocket2);

        // Get the second worker socket
        WebSocket fetchedWorkerSocket2 = manager.getSocketForWorker(workerID2).get();
        Tester.isFalse("Fetched sockets from different workers are not the same",
                fetchedWorkerSocket == fetchedWorkerSocket2);
    }

}
