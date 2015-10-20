package io.vivarium.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.client.Worker;
import io.vivarium.net.Constants;
import io.vivarium.net.common.Pledge;

public class Server extends WebSocketServer
{
    private final static InetSocketAddress PORT = new InetSocketAddress(Constants.DEFAULT_PORT);

    private int i = 0;
    private ObjectMapper mapper = new ObjectMapper();

    public Server() throws UnknownHostException
    {
        super(PORT);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("SERVER: Web Socket Connection Oopened. " + conn + " ~ " + handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println(
                "SERVER: Web Socket Connection closed. " + conn + " ~ " + code + " # " + reason + " & " + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        try
        {
            JSONObject json = new JSONObject(message);
            String type = json.get("type").toString();
            switch (type)
            {
                case Pledge.TYPE:
                    System.out.println("It's a pledge!");
                    Pledge pledge = mapper.readValue(message, Pledge.class);
                    System.out.println("Worker .... " + pledge.workerID + " has said it will do my bidding!");
                    break;
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("SERVER: Web Socket Message . " + conn + " ~ " + message);
        if (i < 3)
        {
            conn.send("REPLY FROM SERVER! + " + i);
            i++;
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println("SERVER: Web Socket Error . " + conn + " ~ " + ex);
    }

    public static void main(String[] args)
    {
        System.out.println("SERVER: Running Vivarium Research Server.");
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
