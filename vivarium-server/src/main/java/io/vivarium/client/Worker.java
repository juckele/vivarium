/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.UUID;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.Constants;
import io.vivarium.net.common.messages.Pledge;

public class Worker extends WebSocketClient
{
    private UUID _workerID = UUID.randomUUID();
    private ObjectMapper mapper = new ObjectMapper();

    public Worker() throws URISyntaxException
    {
        super(new URI("http", null, "localhost", Constants.DEFAULT_PORT, "/", null, null));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.out.println("WORKER: Shake it Open " + handshakedata);
        try
        {
            this.send(mapper.writeValueAsString(new Pledge(_workerID)));
        }
        catch (NotYetConnectedException | JsonProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message)
    {
        System.out.println("WORKER: Message the Message " + message);
        // this.send("Reply to mesmes!");
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("WORKER: Close it down " + code + " / " + reason + " + " + remote);
    }

    @Override
    public void onError(Exception ex)
    {
        System.out.println("WORKER: ERROR " + ex);
    }

    public static void main(String[] args)
    {
        try
        {
            Worker worker = new Worker();
            worker.connect();

        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
