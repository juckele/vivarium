/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.db.DatabaseUtils;
import io.vivarium.db.model.Resource;
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

    private Map<UUID, Pledge> workers = new HashMap<UUID, Pledge>();

    private ObjectMapper mapper = new ObjectMapper();

    private Connection _connection;

    public Server() throws UnknownHostException
    {
        super(PORT);
        try
        {
            _connection = DatabaseUtils.createDatabaseConnection("vivarium", "vivarium", "lifetest");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
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
                String dataString = sendResourceMessage.dataString;
                String jsonString;
                if (sendResourceMessage.resourceFormat == ResourceFormat.JSON)
                {
                    jsonString = sendResourceMessage.dataString;
                }
                else if (sendResourceMessage.resourceFormat == ResourceFormat.GWT_STREAM)
                {
                    VivariumObjectCollection collection = (VivariumObjectCollection) Streamer.get()
                            .fromString(dataString);
                    jsonString = JSONConverter.serializerToJSONString(collection, sendResourceMessage.resourceID);
                }
                else
                {
                    throw new IllegalStateException("Unexpected resource format " + sendResourceMessage.resourceFormat);
                }
                Resource.create(sendResourceMessage.resourceID, jsonString).persistToDatabase(_connection);
            }
            else if (untypedMessage instanceof RequestResource)
            {
                RequestResource requestResourceMessage = (RequestResource) untypedMessage;
                UUID resourceID = requestResourceMessage.resourceID;
                Optional<Resource> resource = Resource.getFromDatabase(_connection, resourceID);
                if (resource.isPresent() && resource.get().jsonData.isPresent())
                {
                    ResourceFormat resourceFormat = requestResourceMessage.resourceFormat;
                    String jsonString = resource.get().jsonData.get();
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
        catch (IOException | SQLException e)
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
        ex.printStackTrace();
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
