/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

public class InboundNetworkListener
{
    public void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
    }

    public void onClose(OutboundNetworkConnection outboundNetworkConnection, int code, String reason, boolean remote)
    {
    }

    public void onMessage(OutboundNetworkConnection outboundNetworkConnection, String message)
    {
    }

    public void onError(OutboundNetworkConnection outboundNetworkConnection, Exception ex)
    {
    }
}
