package io.vivarium.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import vivarium.io.net.Constants;

public class Worker extends WebSocketClient
{
    public Worker() throws URISyntaxException
    {
        super(new URI("http", null, "localhost", Constants.DEFAULT_PORT, "/", null, null));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        System.err.println("Shake it Open " + handshakedata);
    }

    @Override
    public void onMessage(String message)
    {
        System.err.println("Message the Message " + message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.err.println("Close it down " + code + " / " + reason + " + " + remote);
    }

    @Override
    public void onError(Exception ex)
    {
        System.err.println("ERROR " + ex);
    }

    public static void main(String[] args)
    {
        try
        {
            Worker worker = new Worker();

        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
