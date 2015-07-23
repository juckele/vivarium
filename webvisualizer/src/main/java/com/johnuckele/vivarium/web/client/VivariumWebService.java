package com.johnuckele.vivarium.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet") public interface VivariumWebService extends RemoteService
{
	int getWorldSize() throws IllegalArgumentException;
}
