package io.vivarium.net;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import io.vivarium.test.FastTest;
import io.vivarium.test.UnitTest;

public class ServerWebSocketManagerTest
{
    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testSocketOpenClose() throws IOException, InterruptedException
    {
        InetSocketAddress socketAddress = new InetSocketAddress(Constants.DEFAULT_PORT + 1);
        InboundNetworkListener networkListener = mock(InboundNetworkListener.class);
        ServerWebSocketManager socketManager = new ServerWebSocketManager(socketAddress, networkListener);
        socketManager.start();
        socketManager.stop();
    }
}
