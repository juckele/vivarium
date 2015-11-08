package io.vivarium.net.common.messages;

import java.util.UUID;

public class RequestResource extends Message
{
    final public UUID resourceID;

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private RequestResource()
    {
        resourceID = null;
    }

    public RequestResource(UUID resourceID)
    {
        this.resourceID = resourceID;
    }
}
