/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client.task;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.ExecutionException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vivarium.client.TaskClient;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.concurrency.SimpleFuture;

public class DownloadResourceTask extends Task
{
    private final UUID _uuid;
    private SimpleFuture<VivariumObjectCollection> _objects;

    public DownloadResourceTask(UUID uuid)
    {
        _uuid = uuid;
        _objects = new SimpleFuture<>();
    }

    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
        try
        {
            RequestResourceMessage request = new RequestResourceMessage(_uuid, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(request));
        }
        catch (NotYetConnectedException | JsonProcessingException e)
        {
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
                String jsonDataString = sendResource.getDataString();
                VivariumObjectCollection collection = JSONConverter.jsonStringToSerializerCollection(jsonDataString);
                _objects.put(collection);
            }
        }
        catch (IOException e)
        {
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

    public VivariumObjectCollection waitForResource() throws InterruptedException, ExecutionException
    {
        return _objects.get();
    }
}
