package io.vivarium.net;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public class NetworkModuleTest
{
    @Test
    @SuppressWarnings("unchecked")
    public void testAddMessageListener() throws Exception
    {
        // Build a network module
        InboundNetworkListener inboundNetworkListener = mock(InboundNetworkListener.class);
        ObjectMapper objectMapper = new ObjectMapper();
        NetworkModule networkModule = new ServerNetworkModule(inboundNetworkListener, objectMapper);

        // Build and set up the message listener
        Message message = new TestMessage(UUID.randomUUID());
        MessageListener<TestMessage> listener = mock(MessageListener.class);
        networkModule.addMessageListener(listener, TestMessage.class);

        // Send the message to the network module
        networkModule.onMessage(null, objectMapper.writeValueAsString(message));

        // Verify that the listener was called
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        verify(listener).onMessage(eq(null), argument.capture());
        assertEquals(message.getClass(), argument.getValue().getClass());
        assertEquals(message.getMessageID(), argument.getValue().getMessageID());
    }

    private static class TestMessage extends Message
    {
        @JsonCreator
        protected TestMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID)
        {
            super(messageID);
        }
    }
}
