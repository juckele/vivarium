/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.core.Blueprint;
import io.vivarium.net.Constants;
import io.vivarium.net.common.messages.RequestResource;
import io.vivarium.net.common.messages.SendResource;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.util.UUID;

public class Client extends WebSocketClient
{
    // private UUID _clientID = UUID.randomUUID();
    private ObjectMapper mapper = new ObjectMapper();

    public Client() throws URISyntaxException
    {
        super(new URI("ws", null, "localhost", Constants.DEFAULT_PORT, "/", null, null));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.out.println("CLIENT: connection opened " + handshakedata);
        try
        {
            UUID resourceID = UUID.randomUUID();

            // Create a world and upload it
            Blueprint blueprint = Blueprint.makeDefault();
            String jsonString = JSONConverter.serializerToJSONString(blueprint, resourceID);
            SendResource uploadBlueprint = new SendResource(resourceID, jsonString);
            this.send(mapper.writeValueAsString(uploadBlueprint));

            // Let's try getting the resource we just uploaded to make sure it works...
            RequestResource downloadBlueprint = new RequestResource(resourceID);
            this.send(mapper.writeValueAsString(downloadBlueprint));
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
        System.out.println("CLIENT: Received message " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("CLIENT: connection to server closed " + code + " / " + reason + " + " + remote);
    }

    @Override
    public void onError(Exception ex)
    {
        System.out.println("CLIENT: error " + ex);
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
