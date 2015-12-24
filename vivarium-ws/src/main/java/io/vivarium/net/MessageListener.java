/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net;

import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public interface MessageListener<T extends Message>
{
    void onMessage(UUID clientID, T message);
}
