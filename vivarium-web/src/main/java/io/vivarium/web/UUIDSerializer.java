package io.vivarium.web;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.github.nmorel.gwtjackson.client.JsonSerializationContext;
import com.github.nmorel.gwtjackson.client.JsonSerializer;
import com.github.nmorel.gwtjackson.client.JsonSerializerParameters;
import com.github.nmorel.gwtjackson.client.ser.UUIDJsonSerializer;
import com.github.nmorel.gwtjackson.client.stream.JsonWriter;

public class UUIDSerializer extends JsonSerializer<UUID>
{

    private static final UUIDSerializer INSTANCE = new UUIDSerializer();

    /**
     * @return an instance of {@link UUIDJsonSerializer}
     */
    public static UUIDSerializer getInstance()
    {
        return INSTANCE;
    }

    private UUIDSerializer()
    {
    }

    @Override
    public void doSerialize(JsonWriter writer, @Nonnull UUID value, JsonSerializationContext ctx,
            JsonSerializerParameters params)
    {
        writer.unescapeValue(value.toString());
    }
}
