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
import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.net.Constants;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.Pledge;
import io.vivarium.net.messages.RequestResource;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResource;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;

public class Server extends WebSocketServer
{
    private final static InetSocketAddress PORT = new InetSocketAddress(Constants.DEFAULT_PORT);

    private Map<UUID, JsonNode> resources = new HashMap<UUID, JsonNode>();
    private Map<UUID, Pledge> workers = new HashMap<UUID, Pledge>();

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
                String jsonString = sendResourceMessage.dataString;
                JsonNode jsonData = mapper.readTree(jsonString);
                resources.put(sendResourceMessage.resourceID, jsonData);
            }
            else if (untypedMessage instanceof RequestResource)
            {
                RequestResource requestResourceMessage = (RequestResource) untypedMessage;
                if (resources.containsKey(requestResourceMessage.resourceID))
                {
                    ResourceFormat resourceFormat = requestResourceMessage.resourceFormat;
                    String jsonString = resources.get(requestResourceMessage.resourceID).toString();
                    String dataString = null;
                    if (resourceFormat == ResourceFormat.JSON)
                    {
                        dataString = jsonString;
                    }
                    else if (resourceFormat == ResourceFormat.GWT_STREAM)
                    {
                        VivariumObjectCollection collection = JSONConverter
                                .jsonStringToSerializerCollection(jsonString);
                        dataString = Streamer.get().toString(collection);
                    }
                    else
                    {
                        throw new IllegalStateException("Unexpected resource format " + resourceFormat);
                    }
                    SendResource response = new SendResource(requestResourceMessage.resourceID, dataString,
                            resourceFormat);
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
        System.out.println(
                "SERVER: Web Socket Message . " + conn + " ~ " + message.substring(0, Math.min(message.length(), 200)));
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
