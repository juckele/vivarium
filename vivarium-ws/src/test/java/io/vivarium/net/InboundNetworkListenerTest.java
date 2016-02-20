package io.vivarium.net;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.java_websocket.handshake.Handshakedata;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import io.vivarium.test.FastTest;
import io.vivarium.test.UnitTest;

public class InboundNetworkListenerTest
{
    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testOpen()
    {
        NetworkModule networkModule = mock(NetworkModule.class);
        InboundNetworkListener listener = new InboundNetworkListener();
        listener.setNetworkModule(networkModule);
        OutboundNetworkConnection outboundNetworkConnection = mock(OutboundNetworkConnection.class);
        Handshakedata handshakeData = mock(Handshakedata.class);
        listener.onOpen(outboundNetworkConnection, handshakeData);
        verify(networkModule).onOpen(outboundNetworkConnection, handshakeData);
    }

    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testMessage()
    {
        NetworkModule networkModule = mock(NetworkModule.class);
        InboundNetworkListener listener = new InboundNetworkListener();
        listener.setNetworkModule(networkModule);
        OutboundNetworkConnection outboundNetworkConnection = mock(OutboundNetworkConnection.class);
        String message = "Message";
        listener.onMessage(outboundNetworkConnection, message);
        verify(networkModule).onMessage(outboundNetworkConnection, message);
    }

    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testClose()
    {
        NetworkModule networkModule = mock(NetworkModule.class);
        InboundNetworkListener listener = new InboundNetworkListener();
        listener.setNetworkModule(networkModule);
        OutboundNetworkConnection outboundNetworkConnection = mock(OutboundNetworkConnection.class);
        int code = 0;
        String reason = "reason";
        boolean remote = false;
        listener.onClose(outboundNetworkConnection, code, reason, remote);
        verify(networkModule).onClose(outboundNetworkConnection, code, reason, remote);
    }

    @Test
    @Category({ FastTest.class, UnitTest.class })
    public void testError()
    {
        NetworkModule networkModule = mock(NetworkModule.class);
        InboundNetworkListener listener = new InboundNetworkListener();
        listener.setNetworkModule(networkModule);
        OutboundNetworkConnection outboundNetworkConnection = mock(OutboundNetworkConnection.class);
        Exception ex = new Exception();
        listener.onError(outboundNetworkConnection, ex);
        verify(networkModule).onError(outboundNetworkConnection, ex);
    }
}
