package io.vivarium.server;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.gwtstreamer.client.Streamer;

import io.vivarium.net.jobs.CreateWorldJob;
import io.vivarium.net.jobs.SimulationJob;
import io.vivarium.net.messages.CreateJobMessage;
import io.vivarium.net.messages.Message;
import io.vivarium.net.messages.RequestResourceMessage;
import io.vivarium.net.messages.ResourceFormat;
import io.vivarium.net.messages.SendResourceMessage;
import io.vivarium.net.messages.WorkerPledgeMessage;
import io.vivarium.persistence.CreateWorldJobModel;
import io.vivarium.persistence.JobModel;
import io.vivarium.persistence.JobStatus;
import io.vivarium.persistence.PersistenceModule;
import io.vivarium.persistence.ResourceModel;
import io.vivarium.persistence.RunSimulationJobModel;
import io.vivarium.persistence.WorkerModel;
import io.vivarium.serialization.JSONConverter;
import io.vivarium.serialization.VivariumObjectCollection;
import io.vivarium.util.UUID;
import io.vivarium.util.Version;
import io.vivarium.util.concurrency.StartableStoppable;
import io.vivarium.util.concurrency.VoidFunctionScheduler;

public class MessageRouter implements StartableStoppable
{
    private final PersistenceModule _persistenceModule;
    private final ClientConnectionManager _connectionManager;
    private final VoidFunctionScheduler _enforcerScheduler;
    private final ObjectMapper mapper = new ObjectMapper();

    public MessageRouter(PersistenceModule persistenceModule, ClientConnectionManager connectionManager,
            VoidFunctionScheduler enforcerScheduler)
    {
        _persistenceModule = persistenceModule;
        _connectionManager = connectionManager;
        _enforcerScheduler = enforcerScheduler;
    }

    @Override
    public void start()
    {
        _connectionManager.start();
        _enforcerScheduler.start();
    }

    @Override
    public void stop()
    {
        _enforcerScheduler.stop();
        _connectionManager.stop();
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("SERVER: Web Socket Connection Opened. " + conn + " ~ " + handshake);
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println(
                "SERVER: Web Socket Connection closed. " + conn + " ~ " + code + " # " + reason + " & " + remote);
    }

    public void onMessage(WebSocket conn, String message)
    {
        try
        {
            Message untypedMessage = mapper.readValue(message, Message.class);
            if (untypedMessage instanceof WorkerPledgeMessage)
            {
                acceptPledge(conn, (WorkerPledgeMessage) untypedMessage);
            }
            else if (untypedMessage instanceof SendResourceMessage)
            {
                acceptResource(conn, (SendResourceMessage) untypedMessage);
            }
            else if (untypedMessage instanceof RequestResourceMessage)
            {
                handleRequestForResource(conn, (RequestResourceMessage) untypedMessage);
            }
            else if (untypedMessage instanceof CreateJobMessage)
            {
                System.out.println("CreateJobMessage: " + message);
                acceptJob(conn, (CreateJobMessage) untypedMessage);
            }
            else
            {
                System.err.println("SERVER: Unhandled message of type " + untypedMessage.getClass().getSimpleName());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(
                "SERVER: Web Socket Message . " + conn + " ~ " + message.substring(0, Math.min(message.length(), 200)));
    }

    private synchronized void acceptPledge(WebSocket webSocket, WorkerPledgeMessage pledge)
    {
        WorkerModel worker = new WorkerModel(pledge.getWorkerID(), pledge.getThroughputs(), pledge.isActive(),
                new Timestamp(System.currentTimeMillis()), pledge.getFileFormatVersion(), pledge.getCodeVersion());
        _persistenceModule.persist(worker);
        _connectionManager.registerWorker(pledge.getWorkerID(), webSocket);
        _enforcerScheduler.execute();
    }

    private void acceptResource(WebSocket webSocket, SendResourceMessage sendResourceMessage)
    {
        String dataString = sendResourceMessage.getDataString();
        String jsonString;
        if (sendResourceMessage.getResourceFormat() == ResourceFormat.JSON)
        {
            jsonString = sendResourceMessage.getDataString();
        }
        else if (sendResourceMessage.getResourceFormat() == ResourceFormat.GWT_STREAM)
        {
            VivariumObjectCollection collection = (VivariumObjectCollection) Streamer.get().fromString(dataString);
            jsonString = JSONConverter.serializerToJSONString(collection);
        }
        else
        {
            throw new IllegalStateException("Unexpected resource format " + sendResourceMessage.getResourceFormat());
        }
        ResourceModel resource = new ResourceModel(sendResourceMessage.getResourceID(), jsonString,
                Version.FILE_FORMAT_VERSION);
        _persistenceModule.persist(resource);
    }

    private void acceptJob(WebSocket conn, CreateJobMessage createJobMessage)
    {
        JobModel job;
        if (createJobMessage.getJob() instanceof SimulationJob)
        {
            SimulationJob simulationJob = (SimulationJob) createJobMessage.getJob();
            job = new RunSimulationJobModel(simulationJob.getJobID(), JobStatus.BLOCKED, (short) 0, null, null, null,
                    simulationJob.getEndTick(), simulationJob.getInputResources(), simulationJob.getOutputResources(),
                    simulationJob.getDependencies());
        }
        else if (createJobMessage.getJob() instanceof CreateWorldJob)
        {
            CreateWorldJob createWorldJob = (CreateWorldJob) createJobMessage.getJob();
            job = new CreateWorldJobModel(createWorldJob.getJobID(), JobStatus.BLOCKED, (short) 0, null, null, null,
                    createWorldJob.getInputResources(), createWorldJob.getOutputResources(),
                    createWorldJob.getDependencies());
        }
        else
        {
            throw new IllegalStateException(
                    "Unexpected job type " + createJobMessage.getJob().getClass().getSimpleName());
        }
        _persistenceModule.persist(job);
        _enforcerScheduler.execute();
    }

    private void handleRequestForResource(WebSocket webSocket, RequestResourceMessage requestResourceMessage)
            throws IOException
    {
        UUID resourceID = requestResourceMessage.getResourceID();
        Optional<ResourceModel> resource = _persistenceModule.fetch(resourceID, ResourceModel.class);
        if (resource.isPresent() && resource.get().jsonData.isPresent())
        {
            ResourceFormat resourceFormat = requestResourceMessage.getResourceFormat();
            String jsonString = resource.get().jsonData.get();
            String dataString = null;
            if (resourceFormat == ResourceFormat.JSON)
            {
                dataString = jsonString;
            }
            else if (resourceFormat == ResourceFormat.GWT_STREAM)
            {
                VivariumObjectCollection collection = JSONConverter.jsonStringToSerializerCollection(jsonString);
                dataString = Streamer.get().toString(collection);
            }
            else
            {
                throw new IllegalStateException("Unexpected resource format " + resourceFormat);
            }
            SendResourceMessage response = new SendResourceMessage(requestResourceMessage.getResourceID(), dataString,
                    resourceFormat);
            webSocket.send(mapper.writeValueAsString(response));
        }
    }

    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println("SERVER: Web Socket Error . " + conn + " ~ " + ex);
        ex.printStackTrace();
    }
}
