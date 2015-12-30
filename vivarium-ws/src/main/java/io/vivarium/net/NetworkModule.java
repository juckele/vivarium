/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.java_websocket.handshake.Handshakedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.messages.Message;

public abstract class NetworkModule
{

    private ObjectMapper _messageMapper = new ObjectMapper();
    private InboundNetworkListener _inboundListener;

    private Map<Class<? extends Message>, List<MessageListener<? extends Message>>> _messageListeners = new HashMap<>();

    public NetworkModule(InboundNetworkListener inboundListener, ObjectMapper _messageMapper)
    {
        _inboundListener = inboundListener;
        _inboundListener.setNetworkModule(this);
    }

    public <T extends Message> void addMessageListener(MessageListener<T> listener, Class<T> messageClazz)
    {
        if (!_messageListeners.containsKey(messageClazz))
        {
            _messageListeners.put(messageClazz, new LinkedList<MessageListener<? extends Message>>());
        }
        List<MessageListener<? extends Message>> listeners = _messageListeners.get(messageClazz);
        listeners.add(listener);
    }

    public <T extends Message> void removeMessageListener(MessageListener<T> listener, Class<T> messageClazz)
    {
        if (_messageListeners.containsKey(messageClazz))
        {
            List<MessageListener<? extends Message>> listeners = _messageListeners.get(messageClazz);
            listeners.remove(listener);
        }
    }

    protected void sendMessage(OutboundNetworkConnection outboundConnection, Message message)
    {
        try
        {
            String dataString = _messageMapper.writeValueAsString(message);
            outboundConnection.send(dataString);
        }
        catch (JsonProcessingException e)
        {
            // TODO: Start using better logging.
            e.printStackTrace();
        }
    }

    abstract void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake);

    public void onClose(OutboundNetworkConnection outboundNetworkConnection, int code, String reason, boolean remote)
    {
        // TODO Auto-generated method stub

    }

    public void onMessage(OutboundNetworkConnection outboundNetworkConnection, String data)
    {
        try
        {
            Message message = _messageMapper.readValue(data, Message.class);
            Class<? extends Message> messageClazz = message.getClass();
            for (Class<? extends Message> listenerClazz : _messageListeners.keySet())
            {
                if (listenerClazz.isAssignableFrom(messageClazz))
                {
                    List<MessageListener<? extends Message>> listeners = _messageListeners.get(listenerClazz);
                    for (MessageListener<? extends Message> listener : listeners)
                    {
                        listener.onMessage(null, message);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void onError(OutboundNetworkConnection outboundNetworkConnection, Exception ex)
    {
        ex.printStackTrace();
    }
}
