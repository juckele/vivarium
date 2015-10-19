package io.vivarium.server;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.client.Worker;
import io.vivarium.net.Constants;
import io.vivarium.net.common.ServerGreeting;

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
        String jsonGreeting = "X";
        try
        {
            jsonGreeting = mapper.writeValueAsString(new ServerGreeting());
        }
        catch (JsonProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn.send("HELLO FROM SERVER " + jsonGreeting);
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
        System.out.println("SERVER: Web Socket Message . " + conn + " ~ " + message);
        if (i < 10)
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
