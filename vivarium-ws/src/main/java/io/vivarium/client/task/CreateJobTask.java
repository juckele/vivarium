/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client.task;

import java.nio.channels.NotYetConnectedException;

import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vivarium.client.TaskClient;
import io.vivarium.net.jobs.Job;
import io.vivarium.net.messages.CreateJob;

public class CreateJobTask extends Task
{
    private Job _job;

    public CreateJobTask(Job job)
    {
        _job = job;
    }

    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
        try
        {
            CreateJob message = new CreateJob(_job);
            client.send(client.getMapper().writeValueAsString(message));
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
        // No reply expected
        // TODO: We should get an ack, and if we don't get the ack we should resend a CreateJob message periodically.
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
