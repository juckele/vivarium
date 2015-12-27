/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.messages.Message;

public abstract class NetworkModule
{
    ObjectMapper _jsonMessageMapper = new ObjectMapper();

    public <T extends Message> void addMessageListener(MessageListener<T> listener, Class<T> messageClazz)
    {
    }

    protected void sendMessage(OutboundNetworkConnection outboundConnection, Message message)
    {
        try
        {
            String dataString = _jsonMessageMapper.writeValueAsString(message);
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

    public void onMessage(OutboundNetworkConnection outboundNetworkConnection, String message)
    {
        // TODO Auto-generated method stub

    }

    public void onError(OutboundNetworkConnection outboundNetworkConnection, Exception ex)
    {
        // TODO Auto-generated method stub

    }
}
