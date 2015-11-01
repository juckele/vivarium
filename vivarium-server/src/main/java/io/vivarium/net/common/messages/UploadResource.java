package io.vivarium.net.common.messages;

import java.util.UUID;

import org.json.JSONObject;

public class UploadResource extends Message
{
    final public UUID resourceID;
    final public JSONObject resource;

    public UploadResource(UUID resourceID, JSONObject resource)
    {
        this.resourceID = resourceID;
        this.resource = resource;
    }
}
