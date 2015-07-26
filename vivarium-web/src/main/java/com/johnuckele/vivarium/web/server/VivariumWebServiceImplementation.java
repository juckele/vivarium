package com.johnuckele.vivarium.web.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.johnuckele.vivarium.web.client.VivariumWebService;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial") public class VivariumWebServiceImplementation extends RemoteServiceServlet implements VivariumWebService
{
	@Override public int getWorldSize() throws IllegalArgumentException
	{
		return 10;
	}
}