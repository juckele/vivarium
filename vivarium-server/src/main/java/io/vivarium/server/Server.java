/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.Constants;
import io.vivarium.net.common.messages.Message;
import io.vivarium.net.common.messages.Pledge;
import io.vivarium.net.common.messages.RequestResource;
import io.vivarium.net.common.messages.SendResource;
import io.vivarium.util.UUID;

public class Server extends WebSocketServer
{
    private final static InetSocketAddress PORT = new InetSocketAddress(Constants.DEFAULT_PORT);

    private Map<UUID, JsonNode> resources = new HashMap<UUID, JsonNode>();
    private Map<UUID, Pledge> workers = new HashMap<UUID, Pledge>();

    private int i = 0;
    private ObjectMapper mapper = new ObjectMapper();

    public Server() throws UnknownHostException
    {
        super(PORT);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("SERVER: Web Socket Connection Opened. " + conn + " ~ " + handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println(
                "SERVER: Web Socket Connection closed. " + conn + " ~ " + code + " # " + reason + " & " + remote);
    }

    @Override
    public synchronized void onMessage(WebSocket conn, String message)
    {
        try
        {
            Message untypedMessage = mapper.readValue(message, Message.class);
            if (untypedMessage instanceof Pledge)
            {
                Pledge pledge = (Pledge) untypedMessage;
                workers.put(pledge.workerID, pledge);
            }
            else if (untypedMessage instanceof SendResource)
            {
                SendResource sendResourceMessage = (SendResource) untypedMessage;
                resources.put(sendResourceMessage.resourceID, sendResourceMessage.jsonData);
            }
            else if (untypedMessage instanceof RequestResource)
            {
                RequestResource requestResourceMessage = (RequestResource) untypedMessage;
                if (resources.containsKey(requestResourceMessage.resourceID))
                {
                    SendResource response = new SendResource(requestResourceMessage.resourceID,
                            resources.get(requestResourceMessage.resourceID));
                    conn.send(mapper.writeValueAsString(response));
                }
            }
            else
            {
                System.err.println("SERVER: Unhandled message of type " + untypedMessage.getClass().getSimpleName());
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("SERVER: Web Socket Message . " + conn + " ~ " + message);
        if (i < 3)
        {
            conn.send("REPLY FROM SERVER! + " + i);
            i++;
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println("SERVER: Web Socket Error . " + conn + " ~ " + ex);
    }

    public static void main(String[] args)
    {
        System.out.println("SERVER: Running Vivarium Research Server.");
        try
        {
            Server server = new Server();
            server.start();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

}
