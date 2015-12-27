package io.vivarium.net;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;

public class ServerWebSocketManagerTest
{
    @Test
    public void testSocketOpenClose() throws IOException, InterruptedException
    {
        InetSocketAddress socketAddress = new InetSocketAddress(Constants.DEFAULT_PORT + 1);
        InboundNetworkListener networkListener = mock(InboundNetworkListener.class);
        ServerWebSocketManager socketManager = new ServerWebSocketManager(socketAddress, networkListener);
        socketManager.start();
        socketManager.stop();
    }
}
