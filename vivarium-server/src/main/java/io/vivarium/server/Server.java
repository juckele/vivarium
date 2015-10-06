package io.vivarium.server;

import io.vivarium.client.Worker;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import vivarium.io.net.Constants;

public class Server extends WebSocketServer
{
    private final static InetSocketAddress PORT = new InetSocketAddress(Constants.DEFAULT_PORT);

    public Server() throws UnknownHostException
    {
        super(PORT);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("Web Socket Connection Oopened. " + conn + " ~ " + handshake);

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println("Web Socket Connection closed. " + conn + " ~ " + code + " # " + reason + " & " + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        System.out.println("Web Socket Message . " + conn + " ~ " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println("Web Socket Error . " + conn + " ~ " + ex);
    }

    public static void main(String[] args)
    {
        System.out.println("Running Vivarium Research Server.");
        try
        {
            Server server = new Server();
            server.start();
        }
        catch (UnknownHostException e1)
        {
            e1.printStackTrace();
        }
        try
        {
            Worker worker = new Worker();
            worker.connect();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

}
