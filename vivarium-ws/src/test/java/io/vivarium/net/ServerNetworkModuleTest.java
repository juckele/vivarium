package io.vivarium.net;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnuckele.vtest.Tester;

import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public class ServerNetworkModuleTest
{
    @Test
    public void testConstruct()
    {
        InboundNetworkListener a = new InboundNetworkListener();
        ObjectMapper b = new ObjectMapper();
        ServerNetworkModule networkModule = new ServerNetworkModule(a, b);
        Tester.isNotNull("NetworkModule can be built", networkModule);

        networkModule.sendMessage(UUID.randomUUID(), mock(Message.class));
    }
}
