package io.vivarium.net.messages;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnuckele.vtest.Tester;

import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.WorkerPledgeMessage;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;

public class WorkerPledgeMessageTest
{
    @Test
    public void testSerializeDeserialize() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        WorkerPledgeMessage pledge = new WorkerPledgeMessage(UUID.randomUUID(), new long[] { 10, 15, 20 });
        String jsonEncoding = mapper.writeValueAsString(pledge);
        Message decodedPledge = mapper.readValue(jsonEncoding, Message.class);
        Tester.equal("Decoded object should be the same as the original object", pledge, decodedPledge);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        WorkerPledgeMessage pledge1;
        Message pledge2;

        UUID messageID = UUID.randomUUID();
        UUID workerID = UUID.randomUUID();
        pledge1 = new WorkerPledgeMessage(messageID, workerID, true, Version.CURRENT_VERSION,
                Version.FILE_FORMAT_VERSION, new long[] { 10, 15, 20 });
        pledge2 = new WorkerPledgeMessage(messageID, workerID, true, Version.CURRENT_VERSION,
                Version.FILE_FORMAT_VERSION, new long[] { 10, 15, 20 });
        Tester.equal("Two messages that are the same are equal", pledge1, pledge2);

        pledge1 = new WorkerPledgeMessage(UUID.randomUUID(), new long[] { 10, 15, 20 });
        pledge2 = new WorkerPledgeMessage(UUID.randomUUID(), new long[] { 10, 15, 20 });
        Tester.notEqual("Two messages with different workerIDs should not be equal", pledge1, pledge2);

        pledge1 = new WorkerPledgeMessage(UUID.randomUUID(), new long[] { 10, 15, 20 });
        pledge2 = null;
        Tester.equal("Messages are equal to themselves", pledge1, pledge1);
        Tester.notEqual("Messages are not equal to null", pledge1, pledge2);
        Tester.notEqual("Messages are not equal to null", pledge2, pledge1);
    }
}
