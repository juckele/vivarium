package io.vivarium.client.task;

import org.java_websocket.handshake.ServerHandshake;

import io.vivarium.client.TaskClient;

public abstract class Task
{
    public abstract void onOpen(TaskClient client, ServerHandshake handshakedata);

    public abstract void onMessage(TaskClient client, String message);

    public abstract void onClose(TaskClient client, int code, String reason, boolean remote);

    public abstract void onError(TaskClient client, Exception ex);

}