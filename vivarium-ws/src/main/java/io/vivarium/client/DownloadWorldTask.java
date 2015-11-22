package io.vivarium.client;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResource;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResource;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;

public class DownloadWorldTask extends ClientTask
{

    @Override
    public void onOpen(Client client, ServerHandshake handshakedata)
    {
        try
        {
            UUID resourceID = UUID.fromString("D51B6B31-84B5-0835-D5D5-05467AB4F04D");
            RequestResource request = new RequestResource(resourceID, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(request));
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
        System.out.println("CLIENT TASK: RECEVING MESSAGES? ");
        try
        {
            Message untypedMessage = client.getMapper().readValue(message, Message.class);
            if (untypedMessage instanceof SendResource)
            {
                SendResource sendResource = (SendResource) untypedMessage;
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
    public void onClose(Client client, int code, String reason, boolean remote)
    {
    }

    @Override
    public void onError(Client client, Exception ex)
    {
    }

}
