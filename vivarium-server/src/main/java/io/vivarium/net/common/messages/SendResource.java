package io.vivarium.net.common.messages;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vivarium.serialization.JSONConverter;

public class SendResource extends Message
{
    final public UUID resourceID;
    final public JsonNode jsonData;

    @JsonRawValue
    public String getJsonData()
    {
        // default raw value: null or "[]"
        return jsonData == null ? null : jsonData.toString();
    }

    @SuppressWarnings("unused") // Used for Jackson deserialization
    private SendResource()
    {
        resourceID = null;
        jsonData = null;
    }

    public SendResource(String jsonString)
    {
        try
        {
            this.jsonData = new ObjectMapper().readTree(jsonString);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to parse JSON");
        }
        this.resourceID = UUID.fromString(new JSONObject(jsonString).get(JSONConverter.ID_KEY).toString());
    }

    public SendResource(UUID resourceID, JsonNode jsonData)
    {
        this.resourceID = resourceID;
        this.jsonData = jsonData;
    }
}
