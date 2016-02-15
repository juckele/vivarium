package io.vivarium.client.task;

import org.java_websocket.handshake.ServerHandshake;

import io.vivarium.client.TaskClient;

public class NullTask extends Task
{
    @Override
    public void onOpen(TaskClient client, ServerHandshake handshakedata)
    {
    }

    @Override
    public void onMessage(TaskClient client, String message)
    {
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
