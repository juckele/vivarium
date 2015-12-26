/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.net.messages.Message;

public class NetworkModule
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

    void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
    }
}
