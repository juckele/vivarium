package io.vivarium.net;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.johnuckele.vtest.Tester;

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
        MessageListener<TestMessage> listener = mock(MessageListener.class);
        networkModule.addMessageListener(listener, TestMessage.class);

        // Send the message to the network module
        Message message = new TestMessage(UUID.randomUUID());
        networkModule.onMessage(null, objectMapper.writeValueAsString(message));

        // Verify that the listener was called
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        verify(listener).onMessage(eq(null), argument.capture());
        assertEquals(message.getClass(), argument.getValue().getClass());
        assertEquals(message.getMessageID(), argument.getValue().getMessageID());

        Tester.pass("The above code should execute without problems.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddRemoveMessageListener() throws Exception
    {
        // Build a network module
        InboundNetworkListener inboundNetworkListener = mock(InboundNetworkListener.class);
        ObjectMapper objectMapper = new ObjectMapper();
        NetworkModule networkModule = new ServerNetworkModule(inboundNetworkListener, objectMapper);

        // Build and set up the message listener
        MessageListener<TestMessage> listener = mock(MessageListener.class);
        networkModule.addMessageListener(listener, TestMessage.class);

        // Remove the message listener
        networkModule.removeMessageListener(listener, TestMessage.class);

        // Send the message to the network module
        Message message = new TestMessage(UUID.randomUUID());
        networkModule.onMessage(null, objectMapper.writeValueAsString(message));

        // Verify that the listener was not called
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        verify(listener, never()).onMessage(eq(null), argument.capture());

        Tester.pass("The above code should execute without problems.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveMessageListener() throws Exception
    {
        // Build a network module
        InboundNetworkListener inboundNetworkListener = mock(InboundNetworkListener.class);
        ObjectMapper objectMapper = new ObjectMapper();
        NetworkModule networkModule = new ServerNetworkModule(inboundNetworkListener, objectMapper);

        // Build the message listener
        MessageListener<TestMessage> listener = mock(MessageListener.class);

        // Remove the message listener without adding it
        networkModule.removeMessageListener(listener, TestMessage.class);

        // Send the message to the network module
        Message message = new TestMessage(UUID.randomUUID());
        networkModule.onMessage(null, objectMapper.writeValueAsString(message));

        // Verify that the listener was not called
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        verify(listener, never()).onMessage(eq(null), argument.capture());

        Tester.pass("The above code should execute without problems.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubclassListenerDoesNotGetCalled() throws Exception
    {
        // Build a network module
        InboundNetworkListener inboundNetworkListener = mock(InboundNetworkListener.class);
        ObjectMapper objectMapper = new ObjectMapper();
        NetworkModule networkModule = new ServerNetworkModule(inboundNetworkListener, objectMapper);

        // Build and set up the message listener
        MessageListener<AdvancedTestMessage> listener = mock(MessageListener.class);
        networkModule.addMessageListener(listener, AdvancedTestMessage.class);

        // Send the message to the network module
        Message message = new TestMessage(UUID.randomUUID());
        networkModule.onMessage(null, objectMapper.writeValueAsString(message));

        // Verify that the listener was not called
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        verify(listener, never()).onMessage(eq(null), argument.capture());

        Tester.pass("The above code should execute without problems.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuperclassListenerDoesGetCalled() throws Exception
    {
        // Build a network module
        InboundNetworkListener inboundNetworkListener = mock(InboundNetworkListener.class);
        ObjectMapper objectMapper = new ObjectMapper();
        NetworkModule networkModule = new ServerNetworkModule(inboundNetworkListener, objectMapper);

        // Build and set up the message listener
        MessageListener<TestMessage> listener = mock(MessageListener.class);
        networkModule.addMessageListener(listener, TestMessage.class);

        // Send the message to the network module
        AdvancedTestMessage message = new AdvancedTestMessage(UUID.randomUUID());
        networkModule.onMessage(null, objectMapper.writeValueAsString(message));

        // Verify that the listener was called
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        verify(listener).onMessage(eq(null), argument.capture());
        assertEquals(message.getClass(), argument.getValue().getClass());
        assertEquals(message.getMessageID(), argument.getValue().getMessageID());

        Tester.pass("The above code should execute without problems.");
    }

    private static class TestMessage extends Message
    {
        @JsonCreator
        protected TestMessage(@JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID)
        {
            super(messageID);
        }
    }

    private static class AdvancedTestMessage extends TestMessage
    {
        @JsonCreator
        protected AdvancedTestMessage(
                @JsonProperty("messageID") @JsonSerialize(using = UUIDSerializer.class) UUID messageID)
        {
            super(messageID);
        }
    }
}
