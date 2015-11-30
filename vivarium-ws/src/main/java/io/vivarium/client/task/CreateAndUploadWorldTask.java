/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client.task;

import java.nio.channels.NotYetConnectedException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vivarium.client.TaskClient;
import io.vivarium.core.Blueprint;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.util.UUID;

public class CreateAndUploadWorldTask extends Task
{

    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
        try
        {
            UUID resourceID = UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d");

            // Create a world and upload it
            Blueprint blueprint = Blueprint.makeDefault();
            World world = new World(blueprint);
            System.out.println("The ULed world has " + world.getCount(EntityType.CREATURE) + " creatures");
            String jsonString = JSONConverter.serializerToJSONString(world, resourceID);
            SendResourceMessage uploadBlueprint = new SendResourceMessage(resourceID, jsonString, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(uploadBlueprint));

            // Let's try getting the resource we just uploaded to make sure it works...
            RequestResourceMessage downloadBlueprint = new RequestResourceMessage(resourceID, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(downloadBlueprint));
        }
        catch (NotYetConnectedException | JsonProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(TaskClient client, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClose(TaskClient client, int code, String reason, boolean remote)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(TaskClient client, Exception ex)
    {
        // TODO Auto-generated method stub

    }

}
