/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import io.vivarium.net.messages.Message;

public class NetworkModule
{
    public <T extends Message> void addMessageListener(MessageListener<T> listener, Class<T> messageClazz)
    {
    }
}
