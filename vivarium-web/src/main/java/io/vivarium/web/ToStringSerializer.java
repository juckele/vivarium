package io.vivarium.web;

import javax.annotation.Nonnull;

import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializer;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;
import com.github.nmorel.gwtjackson.client.stream.JsonWriter;

public class ToStringSerializer extends JsonSerializer<Object>
{
    private static final ToStringSerializer INSTANCE = new ToStringSerializer();

    public static ToStringSerializer getInstance()
    {
        return INSTANCE;
    }

    private ToStringSerializer()
    {
    }

    @Override
    public void doSerialize(JsonWriter writer, @Nonnull Object value, JsonSerializationContext ctx,
            JsonSerializerParameters params)
    {
        writer.unescapeValue(value.toString());
    }
}
