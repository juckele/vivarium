package io.vivarium.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.vivarium.client.VivariumResearchClient;
import io.vivarium.util.concurrency.StartableStoppable;

public class ServerNetworkModule extends WebSocketServer implements StartableStoppable
{
    private final MessageRouter _router;

    public ServerNetworkModule(InetSocketAddress port, MessageRouter router)
    {
        super(port);
        _router = router;
    }

    @Override
    public void start()
    {
        super.start();
        _router.start();
    }

    @Override
    public void stop() throws IOException, InterruptedException
    {
        _router.stop();
        super.stop();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        if (handshake.hasFieldValue(VivariumResearchClient.CLIENT_ID_HEADER))
        {
            System.out.println(handshake.getFieldValue(VivariumResearchClient.CLIENT_ID_HEADER));
        }
        else
        {
            System.out.println("CLIENT DOESN'T HAVE ID");
        }
        _router.onOpen(conn, handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        _router.onClose(conn, code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        _router.onMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        _router.onError(conn, ex);
    }

}
