/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.Constants;
import io.vivarium.net.messages.Pledge;

public class WorkerClient extends WebSocketClient
{
    private final WorkerConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public WorkerClient() throws URISyntaxException
    {
        super(new URI("ws", null, "localhost", Constants.DEFAULT_PORT, "/", null, null));
        config = WorkerConfig.loadWorkerConfig(new File(WorkerConfig.DEFAULT_PATH), true);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.out.println("WORKER: connection opened with client " + handshakedata);
        try
        {
            this.send(mapper.writeValueAsString(new Pledge(config.workerID, config.throughputs)));
        }
        catch (NotYetConnectedException | JsonProcessingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message)
    {
        System.out.println("WORKER: message received " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("WORKER: connection closed " + code + " / " + reason + " + " + remote);
    }

    @Override
    public void onError(Exception ex)
    {
        System.out.println("WORKER: error " + ex);
        ex.printStackTrace();
    }

    public static void main(String[] args)
    {
        try
        {
            WorkerClient worker = new WorkerClient();
            worker.connect();

        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
