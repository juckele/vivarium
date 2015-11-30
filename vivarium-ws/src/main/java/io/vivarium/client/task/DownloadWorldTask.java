/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client.task;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vivarium.client.TaskClient;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;

public class DownloadWorldTask extends Task
{

    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
        try
        {
            UUID resourceID = UUID.fromString("d51b6b31-84b5-0835-d5d5-05467ab4f04d");
            RequestResourceMessage request = new RequestResourceMessage(resourceID, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(request));
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
        try
        {
            Message untypedMessage = client.getMapper().readValue(message, Message.class);
            if (untypedMessage instanceof SendResourceMessage)
            {
                SendResourceMessage sendResource = (SendResourceMessage) untypedMessage;
                String jsonDataString = sendResource.dataString;
                VivariumObjectCollection collection = JSONConverter.jsonStringToSerializerCollection(jsonDataString);
                World world = collection.getFirst(World.class);
                System.out.println("The DLed world has " + world.getCount(EntityType.CREATURE) + " creatures");
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(TaskClient client, int code, String reason, boolean remote)
    {
    }

    @Override
    public void onError(TaskClient client, Exception ex)
    {
    }

}
