package com.johnuckele.vivarium.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface VivariumWebServiceAsync
{
	void getWorldSize(AsyncCallback<Integer> callback);
}
