/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import org.java_websocket.handshake.Handshakedata;

import com.google.common.base.Preconditions;

public class InboundNetworkListener
{
    private NetworkModule _networkModule = null;

    synchronized void setNetworkModule(NetworkModule networkModule)
    {
        Preconditions.checkState(_networkModule == null);
        _networkModule = networkModule;
    }

    public void onOpen(OutboundNetworkConnection outboundNetworkConnection, Handshakedata handshake)
    {
        Preconditions.checkState(_networkModule != null);
        _networkModule.onOpen(outboundNetworkConnection, handshake);
    }

    public void onClose(OutboundNetworkConnection outboundNetworkConnection, int code, String reason, boolean remote)
    {
        Preconditions.checkState(_networkModule != null);
    }

    public void onMessage(OutboundNetworkConnection outboundNetworkConnection, String message)
    {
        Preconditions.checkState(_networkModule != null);
    }

    public void onError(OutboundNetworkConnection outboundNetworkConnection, Exception ex)
    {
        Preconditions.checkState(_networkModule != null);
    }
}
