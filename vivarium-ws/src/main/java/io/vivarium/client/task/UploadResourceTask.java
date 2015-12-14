/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client.task;

import java.nio.channels.NotYetConnectedException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;

import io.vivarium.client.TaskClient;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;

public class UploadResourceTask extends Task
{
    private final UUID _resourceID;
    private final VivariumObjectCollection _objects;

    public UploadResourceTask(UUID resourceID, VivariumObject object)
    {
        Preconditions.checkNotNull(resourceID);
        Preconditions.checkNotNull(object);
        _resourceID = resourceID;
        _objects = new VivariumObjectCollection();
        _objects.add(object);
    }

    public UploadResourceTask(UUID resourceID, VivariumObjectCollection objects)
    {
        Preconditions.checkNotNull(resourceID);
        Preconditions.checkNotNull(objects);
        _resourceID = resourceID;
        _objects = objects;
    }

    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
        try
        {
            // Create a world and upload it
            String jsonString = JSONConverter.serializerToJSONString(_objects);
            SendResourceMessage uploadMessage = new SendResourceMessage(_resourceID, jsonString, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(uploadMessage));
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

    public UUID getResourceUUID()
    {
        return _resourceID;
    }

}
