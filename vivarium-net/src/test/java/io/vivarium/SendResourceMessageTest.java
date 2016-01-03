package io.vivarium;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnuckele.vtest.Tester;

import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.util.UUID;

public class SendResourceMessageTest
{
    @Test
    public void testSerializeDeserialize() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SendResourceMessage sendResource = new SendResourceMessage(UUID.randomUUID(), "The data", ResourceFormat.JSON);
        String jsonEncoding = mapper.writeValueAsString(sendResource);
        Message decodedPledge = mapper.readValue(jsonEncoding, Message.class);
        Tester.equal("Decoded object should be the same as the original object", sendResource, decodedPledge);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        SendResourceMessage message1;
        Message message2;

        UUID messageID = UUID.randomUUID();
        UUID resourceID = UUID.randomUUID();
        message1 = new SendResourceMessage(messageID, resourceID, "The data", ResourceFormat.JSON);
        message2 = new SendResourceMessage(messageID, resourceID, "The data", ResourceFormat.JSON);
        Tester.equal("Two messages that are the same are equal", message1, message2);

        message1 = new SendResourceMessage(UUID.randomUUID(), "The data", ResourceFormat.JSON);
        message2 = new SendResourceMessage(UUID.randomUUID(), "The data", ResourceFormat.JSON);
        Tester.notEqual("Two messages with different workerIDs should not be equal", message1, message2);

        message1 = new SendResourceMessage(UUID.randomUUID(), "The data", ResourceFormat.JSON);
        message2 = null;
        Tester.equal("Messages are equal to themselves", message1, message1);
        Tester.notEqual("Messages are not equal to null", message1, message2);
        Tester.notEqual("Messages are not equal to null", message2, message1);
    }
}
