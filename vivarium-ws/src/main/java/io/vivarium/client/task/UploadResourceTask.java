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
import io.vivarium.util.UUID;

public class UploadResourceTask extends Task
{
    private final VivariumObject _object;

    public UploadResourceTask(VivariumObject object)
    {
        Preconditions.checkNotNull(object);
        _object = object;
    }

    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
        try
        {
            UUID resourceID = _object.getUUID();

            // Create a world and upload it
            String jsonString = JSONConverter.serializerToJSONString(_object, resourceID);
            SendResourceMessage uploadMessage = new SendResourceMessage(resourceID, jsonString, ResourceFormat.JSON);
            client.send(client.getMapper().writeValueAsString(uploadMessage));

            // Let's try getting the resource we just uploaded to make sure it works...
            // RequestResourceMessage downloadBlueprint = new RequestResourceMessage(resourceID, ResourceFormat.JSON);
            // client.send(client.getMapper().writeValueAsString(downloadBlueprint));
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
        return _object.getUUID();
    }

}
