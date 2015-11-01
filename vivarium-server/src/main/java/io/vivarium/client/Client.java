/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.Constants;
import io.vivarium.net.common.jobs.CreateWorldJob;
import io.vivarium.net.common.jobs.Job;
import io.vivarium.net.common.messages.CreateJob;

public class Client extends WebSocketClient
{
    // private UUID _clientID = UUID.randomUUID();
    private ObjectMapper mapper = new ObjectMapper();

    public Client() throws URISyntaxException
    {
        super(new URI("http", null, "localhost", Constants.DEFAULT_PORT, "/", null, null));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.err.println("CLIENT: Shake it Open " + handshakedata);
        try
        {
            List<Job> a = new LinkedList<Job>();
            UUID b = UUID.randomUUID();
            UUID c = UUID.randomUUID();
            Job job = new CreateWorldJob(a, b, c);
            this.send(mapper.writeValueAsString(new CreateJob(job)));
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
        System.err.println("CLIENT: Message the Message " + message);
        // this.send("Reply to mesmes!");
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.err.println("CLIENT: Close it down " + code + " / " + reason + " + " + remote);
    }

    @Override
    public void onError(Exception ex)
    {
        System.err.println("CLIENT: ERROR " + ex);
    }

    public static void main(String[] args)
    {
        try
        {
            Client worker = new Client();
            worker.connect();

        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
