package io.vivarium.net.common.messages;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.vivarium.net.UUIDDeserializer;
import io.vivarium.net.UUIDSerializer;
import io.vivarium.util.UUID;

public class SendResource extends Message
{
    @JsonSerialize(using = UUIDSerializer.class)
    @JsonDeserialize(using = UUIDDeserializer.class)
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

    public SendResource(UUID resourceID, String jsonString)
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
        this.resourceID = resourceID;
    }

    @JsonCreator
    public SendResource(@JsonProperty("resourceID") UUID resourceID, @JsonProperty("jsonData") JsonNode jsonData)
    {
        this.resourceID = resourceID;
        this.jsonData = jsonData;
    }
}
