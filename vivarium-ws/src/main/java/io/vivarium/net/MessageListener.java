package io.vivarium.net;

import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public interface MessageListener<T extends Message>
{
    void onMessage(UUID clientID, T message);

    @SuppressWarnings("unchecked")
    default void onMessage(UUID clientID, Object message)
    {
        onMessage(clientID, (T) message);
    }
}
