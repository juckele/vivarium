/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.client.task.NullTask;
import io.vivarium.client.task.Task;
import io.vivarium.net.Constants;
import io.vivarium.util.UUID;

public class TaskClient extends WebSocketClient
{
    private ObjectMapper _mapper = new ObjectMapper();
    private Task _task;

    public TaskClient(Task task) throws URISyntaxException
    {
        super(new URI("ws", null, "localhost", Constants.DEFAULT_PORT, "/", null, null), new Draft_10(),
                uuidMap(UUID.randomUUID()), 0);
        this._task = task;
    }

    private static Map<String, String> uuidMap(UUID clientID)
    {
        Map<String, String> map = new HashMap<>();
        map.put("clientID", clientID.toString());
        return map;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.out.println("CLIENT: connection opened " + handshakedata);
        _task.onOpen(this, handshakedata);
    }

    @Override
    public void onMessage(String message)
    {
        System.out.println("CLIENT: Received message " + message.substring(0, Math.min(message.length(), 200)));
        System.out.println("task is " + _task);
        _task.onMessage(this, message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("CLIENT: connection to server closed " + code + " / " + reason + " + " + remote);
        _task.onClose(this, code, reason, remote);
    }

    @Override
    public void onError(Exception ex)
    {
        System.out.println("CLIENT: error " + ex);
        ex.printStackTrace();
        _task.onError(this, ex);
    }

    public static void main(String[] args)
    {
        try
        {
            TaskClient worker = new TaskClient(new NullTask());
            worker.connect();

        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    public ObjectMapper getMapper()
    {
        return _mapper;
    }
}
