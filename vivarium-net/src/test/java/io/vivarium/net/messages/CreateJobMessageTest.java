package io.vivarium.net.messages;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnuckele.vtest.Tester;

import io.vivarium.net.messages.CreateJobMessage;
import io.vivarium.net.messages.Message;
import io.vivarium.util.UUID;

public class CreateJobMessageTest
{
    @Test
    public void testSerializeDeserialize() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        CreateJobMessage createJob = new CreateJobMessage(null);
        String jsonEncoding = mapper.writeValueAsString(createJob);
        Message decodedPledge = mapper.readValue(jsonEncoding, Message.class);
        Tester.equal("Decoded object should be the same as the original object", createJob, decodedPledge);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        CreateJobMessage message1;
        Message message2;

        UUID messageID = UUID.randomUUID();
        message1 = new CreateJobMessage(messageID, null);
        message2 = new CreateJobMessage(messageID, null);
        Tester.equal("Two messages that are the same are equal", message1, message2);

        message1 = new CreateJobMessage(UUID.randomUUID(), null);
        message2 = new CreateJobMessage(UUID.randomUUID(), null);
        Tester.notEqual("Two messages with different workerIDs should not be equal", message1, message2);

        message1 = new CreateJobMessage(null);
        message2 = null;
        Tester.equal("Messages are equal to themselves", message1, message1);
        Tester.notEqual("Messages are not equal to null", message1, message2);
        Tester.notEqual("Messages are not equal to null", message2, message1);
    }
}
