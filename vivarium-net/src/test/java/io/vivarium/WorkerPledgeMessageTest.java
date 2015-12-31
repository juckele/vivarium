package io.vivarium;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnuckele.vtest.Tester;

import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.WorkerPledgeMessage;
import io.vivarium.util.UUID;

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
        WorkerPledgeMessage pledge1 = new WorkerPledgeMessage(UUID.randomUUID(), new long[] { 10, 15, 20 });
        WorkerPledgeMessage pledge2 = new WorkerPledgeMessage(UUID.randomUUID(), new long[] { 10, 15, 20 });
        Tester.notEqual("Two objects with different workerIDs should not be the same", pledge1, pledge2);
    }
}
