/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.nio.channels.NotYetConnectedException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vivarium.core.Blueprint;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.net.messages.RequestResource;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResource;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.util.UUID;

public class CreateAndUploadWorldTask extends ClientTask
{

    @Override
    public void onOpen(Client client, ServerHandshake handshakedata)
    {
        try
        {
            UUID resourceID = UUID.fromString("D51B6B31-84B5-0835-D5D5-05467AB4F04D");

            // Create a world and upload it
            Blueprint blueprint = Blueprint.makeDefault();
            World world = new World(blueprint);
            System.out.println("The ULed world has " + world.getCount(EntityType.CREATURE) + " creatures");
            String jsonString = JSONConverter.serializerToJSONString(world, resourceID);
            SendResource uploadBlueprint = new SendResource(resourceID, jsonString, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(uploadBlueprint));

            // Let's try getting the resource we just uploaded to make sure it works...
            RequestResource downloadBlueprint = new RequestResource(resourceID, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(downloadBlueprint));
        }
        catch (NotYetConnectedException | JsonProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Client client, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClose(Client client, int code, String reason, boolean remote)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(Client client, Exception ex)
    {
        // TODO Auto-generated method stub

    }

}
